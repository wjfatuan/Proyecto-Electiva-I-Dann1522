import re
from datetime import datetime, timedelta, timezone

import jwt
from flask import Flask, jsonify, make_response, request
from flaskext.mysql import MySQL

SECRET_KEY = "CRISTO"

app = Flask(__name__)

app.config['MYSQL_DATABASE_HOST'] = 'localhost'
app.config['MYSQL_DATABASE_USER'] = 'root'
app.config['MYSQL_DATABASE_PASSWORD'] = ''
app.config['MYSQL_DATABASE_DB'] = 'db_flickzy'
app.config['MYSQL_DATABASE_PORT'] = 3306

mysql = MySQL()
mysql.init_app(app)

def validate_username(name: str):
    user_regex = r"(^[a-zA-Z][a-zA-Z0-9_]{4,40}$)"
    if not re.fullmatch(user_regex, name):
        return "Invalid username"
    return None

def validate_mail(mail: str):
    cor_regex = r"(^[a-zA-Z0-9._%+-]+@[a-zA-Z]+\.[a-zA-Z]{2,}$)"
    if not re.fullmatch(cor_regex, mail):
        return "Invalid mail"
    return None

def validate_string(string: str):
    return isinstance(string, str)

def validate_register(name: str, mail: str, password: str):
    errors = []
    name = name.strip() if validate_string(name) else ''
    mail = mail.strip() if validate_string(mail) else ''
    password = password.strip() if validate_string(password) else ''
    error_username = validate_username(name)
    error_mail = validate_mail(mail)
    if error_username:
        errors.append(error_username)
    if error_mail:
        errors.append(error_mail)
    if password == '':
        errors.append("Invalid password")
    if not errors:
        return True, None
    else:
        return False, errors
    
def validate_login(mail: str, password: str):
    errors = []
    mail = mail.strip() if validate_string(mail) else ''
    error_mail = validate_mail(mail)
    if error_mail:
        errors.append(error_mail)
    if password == '':
        errors.append("Invalid password")
    if not errors:
        return True, None
    else:
        return False, errors

def generate_online_token(information: dict[str, any]):
    online_token = jwt.encode(
        {
            **information,
            "type": "online_token",
            "exp" : datetime.now(timezone.utc) + timedelta(minutes= 15)
        },
        SECRET_KEY,
        algorithm= "HS256"
    )
    return online_token

def generate_persistent_token(information: dict[str, any]):
    persistent_token = jwt.encode(
        {
            **information,
            "type": "persistent_token",
            "exp" : datetime.now(timezone.utc) + timedelta(days= 30)
        },
        SECRET_KEY,
        algorithm= "HS256"
    )
    return persistent_token

def generate_tokens(information: dict[str, any]):
    online_token = generate_online_token(information)
    persistent_token = generate_persistent_token(information)
    return online_token, persistent_token

def verifyToken(token: str):
    try:
        decode = jwt.decode(token, SECRET_KEY, algorithms=["HS256"])
        expiration = decode.get("exp", None)
        online_token = None
        if expiration:
            if datetime.fromtimestamp(expiration, timezone.utc) <= datetime.now(timezone.utc) + timedelta(minutes= 2):
                online_token = generate_online_token({
                    "id" : decode["id"],
                    "username" : decode["username"],
                    "mail" : decode["mail"]
                })
        return {
            "message" : "Verified",
            "online_token" : online_token
            }, 200
    except jwt.ExpiredSignatureError:
        return {"errors" : ["Online expired"]}, 401
    except jwt.InvalidTokenError:
        return {"errors" : ["Invalid token"]}, 401

def verifySession():
    auth_header = request.headers.get("Authorization", None)
    if not auth_header:
        return {"errors" : ["Missing authorization header"]}, 400
    parts = auth_header.split()
    if parts[0].lower() != "bearer" or len(parts) != 2:
        print(parts)
        return {"errors" : ["Invalid Authorization Header"]}, 400
    return verifyToken(parts[1])

def set_online(mail: str):
    conn = mysql.connect()
    cursor = conn.cursor()
    query = "UPDATE users SET online = 1 WHERE mail = %s"
    cursor.execute(query, (mail))
    conn.commit()
    cursor.close()
    conn.close()

def set_disonnected(mail: str):
    conn = mysql.connect()
    cursor = conn.cursor()
    query = "UPDATE users SET online = 0 WHERE mail = %s"
    cursor.execute(query, (mail))
    conn.commit()
    cursor.close()
    conn.close()

@app.route("/")
def index():
    response, code = verifySession()
    return jsonify(response), code

@app.route("/user", methods = ['POST'])
def user():
    data = request.get_json()
    my_id = data.get('my_id', '')
    chat_id = data.get('chat_id', '')
    conn = mysql.connect()
    cursor = conn.cursor()
    query = """SELECT
                CASE
                    WHEN chats.user_1 != %s THEN user1.name
                    ELSE user2.name
                END AS other_user_name,
                CASE
                    WHEN chats.user_1 != %s THEN user1.online
                    ELSE user2.online
                END AS online_status
            FROM chats
            JOIN users AS user1 ON user1.id = chats.user_1
            JOIN users AS user2 ON user2.id = chats.user_2
            WHERE chats.id = %s"""
    cursor.execute(query, (my_id, my_id, chat_id))
    row = cursor.fetchone()
    columns = [i[0] for i in cursor.description]
    response = dict(zip(columns, row)) if row else None
    cursor.close()
    conn.close()
    return jsonify(response)

@app.route("/users")
def users():
    response, code = verifySession()
    if code != 200:
        return jsonify(response), code
    new_token = response.get("online_token", None)
    conn = mysql.connect()
    cursor = conn.cursor()
    search = request.args.get("search", None)
    if search:
        like = f"%{search}%"
        query = "SELECT id, name, mail, online FROM users WHERE name LIKE %s OR mail LIKE %s"
        cursor.execute(query, (like, like))
    else:
        cursor.execute("SELECT id, name, mail, online FROM users")
    rows = cursor.fetchall()
    columns = [i[0] for i in cursor.description]
    response = [dict(zip(columns, row)) for row in rows]
    cursor.close()
    conn.close()
    response = make_response(jsonify(response))
    if new_token:
        response.headers.add("X-Refresh-Token", new_token)
    return response

@app.route("/autologin")
def autologin():
    auth_header = request.headers.get("Authorization", None)
    if not auth_header:
        return jsonify({"errors" : ["Missing authorization header"]})
    parts = auth_header.split()
    if parts[0].lower() != "bearer" or len(parts) != 2:
        return jsonify({"errors" : ["Invalid Authorization Header"]})
    persistent_token = parts[1]
    try:
        decoded = jwt.decode(persistent_token, SECRET_KEY, algorithms=["HS256"])
        if decoded.get("type", "") == "persistent_token":
            conn = mysql.connect()
            cursor = conn.cursor()
            query = "SELECT COUNT(*) FROM users WHERE mail = %s"
            cursor.execute(query, (decoded['mail']))
            quantity = cursor.fetchone()
            cursor.close()
            conn.close()
            if quantity[0] == 0:
                return jsonify({
                    "errors": ["The user doesn't exists"]
                }), 400
            set_online(decoded["mail"])
            online_token = generate_online_token({
                    "id" : decoded["id"],
                    "username" : decoded["username"],
                    "mail" : decoded["mail"]
            })
            return jsonify({
                "message": "Token valid",
                "id" : decoded["id"],
                "username": decoded["username"],
                "mail": decoded["mail"],
                "online_token" : online_token
            })
        else:
            return jsonify({"errors" : ["Invalid token type"]})
    except jwt.ExpiredSignatureError:
        return jsonify({"errors": ["Token expired"]}), 401
    except jwt.InvalidTokenError:
        return jsonify({"errors": ["Invalid token"]}), 401

@app.route("/login", methods= ['POST'])
def login():
    data = request.get_json()
    mail = data.get('mail', '')
    password = data.get('pass', '')
    is_valid, errors = validate_login(mail, password)
    if is_valid:
        conn = mysql.connect()
        cursor = conn.cursor()
        query = "SELECT COUNT(*) FROM users WHERE mail = %s"
        cursor.execute(query, (mail))
        quantity = cursor.fetchone()
        if quantity[0] < 1:
            cursor.close()
            conn.close()
            return jsonify({"errors" : ["The user no exists"]}), 400
        query = "SELECT id, name, mail FROM users WHERE mail = %s AND pass = %s"
        cursor.execute(query, (mail, password))
        result = cursor.fetchone()
        cursor.close()
        conn.close()
        if result:
            online_token, persistent_token = generate_tokens(
                {
                "id" : result[0],
                "username" : result[1],
                "mail" : result[2]
                }
            )
            set_online(result[2])
            return jsonify({
                "message" : "Login successfull",
                "id" : result[0],
                "username" : result[1],
                "mail" : result[2],
                "online_token" : online_token,
                "persistent_token" : persistent_token
            })
        else:
            return jsonify({"errors" : ["Incorrect password"]}), 400
    else:
        return jsonify({"errors": errors}), 400


@app.route("/register", methods= ['POST'])
def register():
    data = request.get_json()
    name = data.get('name', '')
    mail = data.get('mail', '')
    password = data.get('pass', '')
    is_valid, errors = validate_register(name, mail, password)
    if is_valid:
        conn = mysql.connect()
        cursor = conn.cursor()
        query = "SELECT COUNT(*) FROM users where mail = %s"
        cursor.execute(query, (mail))
        quantity = cursor.fetchone()
        if quantity[0] != 0:
            cursor.close()
            conn.close()
            return jsonify({"errors": ["The user already exists"]}), 400
        query = "INSERT INTO users (`mail`, `name`, `pass`) VALUES (%s,%s,%s)"
        cursor.execute(query, (mail, name, password))
        conn.commit()
        cursor.close()
        conn.close()
        return jsonify({"message" : "User registration successfull"})
    else:
        return jsonify({"errors": errors}), 400

@app.route("/logout")
def logout():
    auth_header = request.headers.get("Authorization", None)
    if not auth_header:
        return jsonify({"errors": "Missing authorization header"}), 401

    parts = auth_header.split()
    if parts[0].lower() != "bearer" or len(parts) != 2:
        return jsonify({"errors": "Invalid Authorization Header"}), 401

    token = parts[1]
    try:
        decoded = jwt.decode(token, SECRET_KEY, algorithms=["HS256"])
        if decoded.get("type") != "online_token":
            return jsonify({"errors": ["Invalid token type"]}), 400

        set_disonnected(decoded["mail"])
        return jsonify({"message": "Logout successful"})

    except jwt.ExpiredSignatureError:
        return jsonify({"errors": ["Token expired"]}), 401
    except jwt.InvalidTokenError:
        return jsonify({"errors": ["Invalid token"]}), 401

@app.route("/new_chat", methods = ['POST'])
def new_chat():
    response, code = verifySession()
    if code != 200:
        return jsonify(response), code
    new_token = response.get("online_token", None)
    data = request.get_json()
    my_id = data.get("my_id", None)
    dest_id = data.get("dest_id", None)
    if my_id is not None and dest_id is not None:
        conn = mysql.connect()
        cursor = conn.cursor()
        query = "SELECT COUNT(*) FROM chats WHERE (user_1 = %s AND user_2 = %s) OR (user_1 = %s AND user_2 = %s)"
        cursor.execute(query, (my_id, dest_id, dest_id, my_id))
        quantity = cursor.fetchone()
        if quantity[0] != 0:
            cursor.close()
            conn.close()
            response = make_response(jsonify({"errors" : ["The chat already exists"]}), 400)
            if new_token:
                response.headers.add("X-Refresh-Token", new_token)
            return response
        query = "INSERT INTO chats (user_1, user_2) VALUES (%s, %s)"
        cursor.execute(query, (my_id, dest_id))
        conn.commit()
        cursor.close()
        conn.close()
        response = make_response(jsonify({"message" : "Chat created succesfully"}))
        if new_token:
            response.headers.add("X-Refresh-Token", new_token)
        return response
    return jsonify({"errors" : ["Invalid id's"]}), 400

@app.route("/chats", methods= ['POST'])
def chats():
    response, code = verifySession()
    if code != 200:
        return jsonify(response), code
    new_token = response.get("online_token", None)
    data = request.get_json()
    my_id = data.get("my_id", None)
    if my_id:
        conn = mysql.connect()
        cursor = conn.cursor()
        query = """SELECT chats.id AS id_chat,
                    COALESCE(m.timestamp, NULL) AS last_message_time,
                    COALESCE(m.content, NULL) AS last_message_text,
                    CASE WHEN chats.user_1 = %s THEN user2.name ELSE user1.name END AS other_user_name
                FROM chats
                JOIN users AS user1 ON user1.id = chats.user_1
                JOIN users AS user2 ON user2.id = chats.user_2
                LEFT JOIN (
                    SELECT id_chat, MAX(timestamp) AS max_time
                    FROM messages
                    GROUP BY id_chat
                ) AS latest ON latest.id_chat = chats.id
                LEFT JOIN messages AS m ON m.id_chat = latest.id_chat AND m.timestamp = latest.max_time 
                WHERE chats.user_1 = %s OR chats.user_2 = %s
                ORDER BY m.timestamp DESC;"""
        cursor.execute(query, (my_id, my_id, my_id))
        rows = cursor.fetchall()
        columns = [i[0] for i in cursor.description]
        result = [dict(zip(columns, row)) for row in rows]
        response = make_response(jsonify(result))
        conn.commit()
        cursor.close()
        conn.close()
        if new_token:
            response.headers.add("X-Refresh-Token", new_token)
        return response
    return jsonify({"errors" : ["Invalid id"]}), 400

@app.route("/messages", methods= ['POST'])
def messages():
    response, code = verifySession()
    if code != 200:
        return jsonify(response), code
    new_token = response.get("online_token", None)
    data = request.get_json()
    chat_id = data.get("chat_id", None)
    if chat_id:
        conn = mysql.connect()
        cursor = conn.cursor()
        query = """
                SELECT * FROM messages WHERE id_chat = %s ORDER BY timestamp
                """
        cursor.execute(query, (chat_id))
        rows = cursor.fetchall()
        columns = [i[0] for i in cursor.description]
        result = [dict(zip(columns, row)) for row in rows]
        response = make_response(jsonify(result))
        conn.commit()
        cursor.close()
        conn.close()
        if new_token:
            response.headers.add("X-Refresh-Token", new_token)
        return response
    return jsonify({"errors" : ["Invalid id"]}), 400

@app.route("/send_message", methods= ['POST'])
def send_message():
    response, code = verifySession()
    if code != 200:
        return jsonify(response), code
    new_token = response.get("online_token", None)
    data = request.get_json()
    chat_id = data.get("chat_id", None)
    sender_id = data.get("sender_id", None)
    content = data.get("content", None)
    if chat_id and sender_id and content:
        conn = mysql.connect()
        cursor = conn.cursor()
        query = """
            INSERT INTO `messages`(`id_chat`, `id_sender`, `content`)
            VALUES (%s, %s, %s)
            """
        cursor.execute(query, (chat_id, sender_id, content))
        response = make_response(jsonify({"message" : "message sended"}))
        conn.commit()
        cursor.close()
        conn.close()
        if new_token:
            response.headers.add("X-Refresh-Token", new_token)
        return response
    return jsonify({"errors" : ["Invalid id"]}), 400

if __name__ == "__main__":
    app.run(host="0.0.0.0", debug= True)
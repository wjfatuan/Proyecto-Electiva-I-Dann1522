# Usa la imagen base de Android desde CircleCI (2024.11.1)
FROM cimg/android:2024.11.1

# Actualiza la lista de paquetes y instala dependencias necesarias para Google Cloud CLI
RUN sudo apt update && sudo apt install -y apt-transport-https ca-certificates gnupg curl

# Agrega la clave de Google Cloud para apt
RUN sudo curl https://packages.cloud.google.com/apt/doc/apt-key.gpg | sudo gpg --dearmor -o /usr/share/keyrings/cloud.google.gpg

# Añade el repositorio de Google Cloud SDK a las fuentes de apt
RUN echo "deb [signed-by=/usr/share/keyrings/cloud.google.gpg] https://packages.cloud.google.com/apt cloud-sdk main" | sudo tee -a /etc/apt/sources.list.d/google-cloud-sdk.list

# Actualiza la lista de paquetes e instala Google Cloud CLI
RUN sudo apt update -y && sudo apt install google-cloud-cli -y


# Proyecto de ejemplo: Flickzy

Este repositorio contiene el código fuente y la documentación del proyecto **Flickzy**, una aplicación de mensajería básica.

## Requisitos

- **SDK de Android**: La aplicación fue probada con la versión 35 (2024.2.11) del SDK de Android.
- **Android Studio**: Se recomienda abrir el proyecto con Android Studio. La versión usada fue Android LadyBug (2024.2.1.11).

## Uso del Proyecto

### Con Android Studio

1. Abre el proyecto en Android Studio.
2. Para compilar el proyecto, ve a **Build > Build APKs**.

### Desde la Línea de Comandos

Nota: Este proyecto también incluye una imagen pre-configurada con el SDK de Android y herramientas de Google Cloud (GCloud).

1. Para compilar el proyecto desde la línea de comandos, ejecuta el siguiente comando de Gradle:

    ```bash
    ./gradlew assembleDebug
    ```

## Funcionalidad de la Aplicación

### Enviar y Recibir Mensajes
Flickzy permite a los usuarios enviar y recibir mensajes en tiempo real. La aplicación está conectada a un servidor para permitir la comunicación instantánea.

### Ver Usuarios Conectados
Los usuarios pueden ver quiénes están conectados en el momento.

### Ver Chats Recientes o Antiguos
Flickzy mantiene un historial de los mensajes, permitiendo ver los chats recientes y los más antiguos.

## Pruebas

### En un Dispositivo Físico o Emulador

Puedes probar la aplicación instalando el archivo APK generado en un dispositivo físico o en un emulador de Android.

### Usando Firebase Test Lab

1. Crea un nuevo proyecto en Firebase.
2. Inicia sesión en Google Cloud desde la línea de comandos:

    ```bash
    gcloud init --console-only
    ```

3. Ejecuta tus pruebas con el siguiente comando, asegurándote de estar en la carpeta donde se generó el APK (por ejemplo, `app/build/outputs/apk/debug`):

    ```bash
    gcloud firebase test android run --app app-debug.apk --type robo --device model=tokay,version=34,locale=en,orientation=portrait
    ```

## Uso de Github Actions

El proyecto está configurado con **GitHub Actions** para compilar y probar automáticamente el proyecto cada vez que se hace un push a la rama principal.

Para configurar las acciones de GitHub:

1. Crea una nueva clave privada en Firebase desde **Project Overview > Project Settings > Service Accounts**.
2. Crea un secreto llamado **GCP_FIREBASE_CREDENTIALS** en tu repositorio de GitHub bajo **Settings > Secrets and Variables > Actions**.
3. Asigna el rol de **Project Editor** en la consola de IAM de Google Cloud: [IAM Console](https://console.cloud.google.com/iam-admin/iam).
4. Habilita la API de **Cloud Tool Results** para tu proyecto en [Tool Results API](https://console.cloud.google.com/apis/api/toolresults.googleapis.com/).

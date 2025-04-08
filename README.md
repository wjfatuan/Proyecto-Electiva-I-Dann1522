name: Flickzy Android CI with Firebase Test Lab

on:
  push:
    branches:
      - "main"
  pull_request:
    branches:
      - "main"

jobs:
  build:
    runs-on: ubuntu-latest

    steps:
      # Paso 1: Checkout del código
      - name: Checkout code
        uses: actions/checkout@v4

      # Paso 2: Configurar JDK 17
      - name: Set up JDK 17
        uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'
          cache: gradle

      # Paso 3: Establecer permisos de ejecución para gradlew
      - name: Grant execute permission for gradlew
        if: ${{ hashFiles('gradlew') != '' }}
        run: chmod +x gradlew

      # Paso 4: Compilar la aplicación con Gradle
      - name: Build APK with Gradle
        if: ${{ hashFiles('build.gradle*') != '' }}
        run: ./gradlew assembleDebug

      # Paso 5: Subir el APK compilado como artefacto
      - name: Upload APK
        uses: actions/upload-artifact@v4
        with:
          name: apk-debug.apk
          path: app/build/outputs/apk/debug/app-debug.apk

  firebase-test-lab:
    needs: build
    runs-on: ubuntu-latest

    steps:
      # Paso 1: Descargar el APK del paso anterior
      - name: Download APK
        uses: actions/download-artifact@v4
        with:
          name: apk-debug.apk

      # Paso 2: Autenticarse en GCP para usar Firebase Test Lab
      - id: 'auth'
        uses: google-github-actions/auth@v2
        with:
          credentials_json: '${{ secrets.GCP_FIREBASE_CREDENTIALS }}'  # Asegúrate de configurar las credenciales en GitHub Secrets

      # Paso 3: Configurar la CLI de Google Cloud SDK
      - name: Set up Cloud SDK
        uses: google-github-actions/setup-gcloud@v2

      # Paso 4: Ejecutar pruebas en Firebase Test Lab
      - name: Run tests in Firebase Test Lab
        run: |
          gcloud firebase test android run \
            --app app-debug.apk \
            --type robo \
            --device model=Pixel2,version=30,locale=en,orientation=portrait

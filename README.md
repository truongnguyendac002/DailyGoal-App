# DailyGoal-App

## Video Demo
[![Watch the video](https://img.youtube.com/vi/dP_Nou8Ij7c/maxresdefault.jpg)](https://youtu.be/dP_Nou8Ij7c)

### [Watch this video on YouTube](https://youtu.be/dP_Nou8Ij7c)

## How to Run the System

### Prerequisites
- **Java**: OpenJDK 17
- **Node.js**: Version v20.13.1
- **Maven**: Version 3.9.6
- **MySQL** (or any other relational database)
- **VNPay Account** (for payment integration)

### Backend (Spring Boot)
1. Open the **backend** folder in IntelliJ IDEA
2. Update the **application.properties** file in **src/main/resources** with your database configuration:
    ```properties
    spring.datasource.username=your_username
    spring.datasource.password=your_password
    ```
3. Run the **CoffeeShopApplication** class
4. The backend will be accessible at http://localhost:8080

### Frontend (ReactJS)
1. Open the **frontend** folder in Visual Studio Code
2. Install Dependencies
    ```bash
    npm install
    ```
3. Start the Frontend
    ```bash
    npm run dev
    ```
4. The frontend will be accessible at http://localhost:5173

## Special Thanks

- **Nguyễn Đắc Trường** *(It's me)*

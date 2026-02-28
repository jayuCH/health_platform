@echo off
REM =========================================
REM   健康饮食管理系统 - 环境配置 (Windows)
REM =========================================

echo.
echo ========================================
echo   健康饮食管理系统 - 环境配置
echo ========================================
echo.

REM 获取当前脚本所在目录
cd /d "%~dp0"
set SCRIPT_DIR=%cd%

REM 检查环境变量
echo 检查环境...
echo.

REM 检查 Java
echo [1/6] 检查 Java...
where java >nul 2>&1
if %ERRORLEVEL% EQU 0 (
    echo [OK] Java 已安装
    java -version
) else (
    echo [FAIL] Java 未安装
    echo.
    echo 请先安装 Java 17+:
    echo 下载地址: https://adoptium.net/
    echo 或: https://www.oracle.com/java/technologies/downloads/
    echo.
    pause
    exit /b 1
)

REM 检查 Maven
echo.
echo [2/6] 检查 Maven...
where mvn >nul 2>&1
if %ERRORLEVEL% EQU 0 (
    echo [OK] Maven 已安装
    mvn -version
) else (
    echo [FAIL] Maven 未安装
    echo.
    echo 请先安装 Maven:
    echo 下载地址: https://maven.apache.org/download.cgi
    echo.
    pause
    exit /b 1
)

REM 检查 Node.js
echo.
echo [3/6] 检查 Node.js...
where node >nul 2>&1
if %ERRORLEVEL% EQU 0 (
    echo [OK] Node.js 已安装
    node -v
) else (
    echo [FAIL] Node.js 未安装
    echo.
    echo 请先安装 Node.js:
    echo 下载地址: https://nodejs.org/
    echo.
    pause
    exit /b 1
)

REM 检查 npm
echo.
echo [4/6] 检查 npm...
where npm >nul 2>&1
if %ERRORLEVEL% EQU 0 (
    echo [OK] npm 已安装
    npm -v
) else (
    echo [FAIL] npm 未安装
)

REM 检查 MySQL
echo.
echo [5/6] 检查 MySQL...
where mysql >nul 2>&1
if %ERRORLEVEL% EQU 0 (
    echo [OK] MySQL 已安装
    mysql --version
) else (
    echo [WARN] MySQL 未找到
    echo 请确保 MySQL 已安装并添加到 PATH 环境变量
)

REM 检查 Redis
echo.
echo [6/6] 检查 Redis...
where redis-server >nul 2>&1
if %ERRORLEVEL% EQU 0 (
    echo [OK] Redis 已安装
) else (
    echo [WARN] Redis 未找到
    echo 请确保 Redis 已安装并添加到 PATH 环境变量
)

echo.
echo ========================================
echo   环境检查完成
echo ========================================
echo.

REM 询问是否继续配置
set /p CONTINUE="是否继续配置数据库? (y/n): "
if /i NOT "%CONTINUE%"=="y" (
    echo 配置已取消
    pause
    exit /b 0
)

REM 配置 MySQL
echo.
echo ========================================
echo   配置 MySQL
echo ========================================
echo.

REM 询问MySQL配置
set /p MYSQL_HOST="MySQL Host (默认: localhost): "
if "%MYSQL_HOST%"=="" set MYSQL_HOST=localhost

set /p MYSQL_PORT="MySQL Port (默认: 3306): "
if "%MYSQL_PORT%"=="" set MYSQL_PORT=3306

set /p MYSQL_USER="MySQL 用户名 (默认: healthy_user): "
if "%MYSQL_USER%"=="" set MYSQL_USER=healthy_user

set /p MYSQL_PASS="MySQL 密码 (默认: healthy123): "
if "%MYSQL_PASS%"=="" set MYSQL_PASS=healthy123

set /p MYSQL_ROOT_PASS="MySQL root密码 (用于创建数据库): "
if "%MYSQL_ROOT_PASS%"=="" (
    echo 请输入MySQL root密码:
    set /p MYSQL_ROOT_PASS="
)

REM 创建数据库和用户
echo 创建数据库和用户...

mysql -u root -p%MYSQL_ROOT_PASS% --host=%MYSQL_HOST% --port=%MYSQL_PORT% -e "CREATE DATABASE IF NOT EXISTS healthy_diet DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"

mysql -u root -p%MYSQL_ROOT_PASS% --host=%MYSQL_HOST% --port=%MYSQL_PORT% -e "CREATE USER IF NOT EXISTS 'healthy_user'@'%' IDENTIFIED BY '%MYSQL_PASS%';"

mysql -u root -p%MYSQL_ROOT_PASS% --host=%MYSQL_HOST% --port=%MYSQL_PORT% -e "GRANT ALL PRIVILEGES ON healthy_diet.* TO 'healthy_user'@'%';"

mysql -u root -p%MYSQL_ROOT_PASS% --host=%MYSQL_HOST% --port=%MYSQL_PORT% -e "FLUSH PRIVILEGES;"

echo.
if %ERRORLEVEL% EQU 0 (
    echo [OK] 数据库配置成功
) else (
    echo [FAIL] 数据库配置失败
    echo 请检查MySQL连接信息和密码
    pause
    exit /b 1
)

REM 导入SQL脚本
echo.
set /p IMPORT_SQL="是否导入初始数据? (y/n): "
if /i "%IMPORT_SQL%"=="y" (
    set SQL_FILE=%SCRIPT_DIR%\backend\src\main\resources\db\migration\init.sql

    if exist "%SQL_FILE%" (
        echo 导入SQL脚本...
        mysql -u %MYSQL_USER% -p%MYSQL_PASS% --host=%MYSQL_HOST% --port=%MYSQL_PORT% healthy_diet < "%SQL_FILE%"

        if %ERRORLEVEL% EQU 0 (
            echo [OK] SQL导入成功
        ) else (
            echo [WARN] SQL导入可能有问题，请手动检查
        )
    ) else (
        echo [WARN] SQL文件不存在: %SQL_FILE%
    )
)

REM 配置 Redis
echo.
echo ========================================
echo   配置 Redis
echo ========================================
echo.

set /p REDIS_HOST="Redis Host (默认: localhost): "
if "%REDIS_HOST%"=="" set REDIS_HOST=localhost

set /p REDIS_PORT="Redis Port (默认: 6379): "
if "%REDIS_PORT%"=="" set REDIS_PORT=6379

set /p REDIS_PASS="Redis 密码 (无密码直接回车): "

echo 测试Redis连接...

redis-cli -h %REDIS_HOST% -p %REDIS_PORT% ping >nul 2>&1
if %ERRORLEVEL% EQU 0 (
    echo [OK] Redis 连接成功
) else (
    echo [WARN] Redis 无法连接，请确保Redis已启动
)

REM 启动 Redis
set /p START_REDIS="是否启动Redis? (y/n): "
if /i "%START_REDIS%"=="y" (
    echo 启动Redis...
    start /B redis-server --port %REDIS_PORT%
    timeout /t 3 /nobreak >nul
)

REM 生成 application.yml
echo.
echo ========================================
echo   生成配置文件
echo ========================================
echo.

set CONFIG_FILE=%SCRIPT_DIR%\backend\src\main\resources\application.yml

REM 备份原文件
if exist "%CONFIG_FILE%" (
    copy "%CONFIG_FILE%" "%CONFIG_FILE%.backup" >nul
    echo 已备份原配置文件
)

REM 生成新的配置文件
(
echo spring:
echo   application:
echo     name: healthy-diet-backend
echo.
echo   # 数据源配置
echo   datasource:
echo     driver-class-name: com.mysql.cj.jdbc.Driver
echo     url: jdbc:mysql://%MYSQL_HOST%:%MYSQL_PORT%/healthy_diet?useUnicode=true^&characterEncoding=utf8^&useSSL=false^&serverTimezone=Asia/Shanghai^&allowPublicKeyRetrieval=true
echo     username: %MYSQL_USER%
echo     password: %MYSQL_PASS%
echo.
echo   # Redis配置
echo   data:
echo     redis:
echo       host: %REDIS_HOST%
echo       port: %REDIS_PORT%
) > "%CONFIG_FILE%"

REM 添加Redis密码（如果有）
if NOT "%REDIS_PASS%"=="" (
    echo       password: %REDIS_PASS% >> "%CONFIG_FILE%"
) else (
    echo       password: "" >> "%CONFIG_FILE%"
)

(
echo       database: 0
echo       lettuce:
echo         pool:
echo           max-active: 8
echo           max-wait: -1ms
echo           max-idle: 8
echo           min-idle: 0
echo.
echo   # 文件上传配置
echo   servlet:
echo     multipart:
echo       max-file-size: 10MB
echo       max-request-size: 10MB
echo.
echo   # Jackson配置
echo   jackson:
echo     time-zone: GMT+8
echo     date-format: yyyy-MM-dd HH:mm:ss
echo.
echo # MyBatis Plus配置
echo mybatis-plus:
echo   mapper-locations: classpath*:/mapper/**/*.xml
echo   type-aliases-package: com.healthydiet.entity
echo   configuration:
echo     map-underscore-to-camel-case: true
echo     log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
echo   global-config:
echo     db-config:
echo       id-type: auto
echo       logic-delete-field: deleted
echo       logic-delete-value: 1
echo       logic-not-delete-value: 0
echo.
echo # 服务器配置
echo server:
echo   port: 8080
echo   servlet:
echo     context-path: /api
echo.
echo # JWT配置
echo jwt:
echo   secret: healthy-diet-secret-key-2024-change-in-production
echo   expiration: 604800000
echo.
echo # AI配置
echo ai:
echo   api-url: https://api.openai.com/v1/chat/completions
echo   api-key: your-api-key-here
echo   model: gpt-3.5-turbo
echo.
echo   # 图像识别配置
echo   image-recognition:
echo     enabled: true
echo     timeout: 30000
echo.
echo # 文件存储配置
echo file:
echo   upload-path: ./uploads/
echo   access-url: http://localhost:8080/api/file/
echo.
echo # 推荐算法配置
echo recommendation:
echo   enabled: true
echo   collaborative-filtering:
echo     min-interactions: 5
echo     similar-users: 10
echo.
echo # 日志配置
echo logging:
echo   level:
echo     com.healthydiet: debug
echo     org.springframework.security: debug
echo   pattern:
echo     console: "%%d{yyyy-MM-dd HH:mm:ss} [%%thread] %%-5level %%logger{36} - %%msg%%n"
echo.
echo # Knife4j配置
echo knife4j:
echo   enable: true
echo   openapi:
echo     title: 健康饮食管理系统API
echo     description: 基于AI的健康饮食管理小程序后端接口文档
echo     version: 1.0.0
echo     concat: contact@healthydiet.com
echo   setting:
echo     language: zh_cn
) >> "%CONFIG_FILE%"

echo.
echo [OK] 配置文件已生成: %CONFIG_FILE%

REM 创建上传目录
echo.
echo 创建必要的目录...
if not exist "%SCRIPT_DIR%\uploads\avatar" mkdir "%SCRIPT_DIR%\uploads\avatar"
if not exist "%SCRIPT_DIR%\uploads\analysis" mkdir "%SCRIPT_DIR%\uploads\analysis"
echo [OK] 目录创建完成

echo.
echo ========================================
echo   配置完成！
echo ========================================
echo.
echo 后续步骤:
echo.
echo 1. 安装后端依赖:
echo    cd backend
echo    mvn clean install
echo.
echo 2. 启动后端服务:
echo    cd backend
echo    mvn spring-boot:run
echo.
echo 3. 安装前端依赖:
echo    cd frontend
echo    npm install
echo.
echo 4. 启动前端服务:
echo    cd frontend
echo    npm run dev:h5
echo.
echo 5. 访问API文档:
echo    http://localhost:8080/api/doc.html
echo.
echo 配置信息:
echo    MySQL Host: %MYSQL_HOST%:%MYSQL_PORT%
echo    MySQL 用户名: %MYSQL_USER%
echo    MySQL 密码: %MYSQL_PASS%
echo    MySQL 数据库: healthy_diet
echo.
echo    Redis Host: %REDIS_HOST%:%REDIS_PORT%
if NOT "%REDIS_PASS%"=="" (
    echo    Redis 密码: %REDIS_PASS%
)
echo.

pause

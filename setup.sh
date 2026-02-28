#!/bin/bash

# 健康饮食管理系统 - 一键本地环境配置脚本
# 适用于 Linux / macOS 系统

set -e

echo "========================================="
echo "  健康饮食管理系统 - 环境配置"
echo "========================================="
echo ""

# 检查操作系统
OS="$(uname -s)"
case "${OS}" in
    Linux*)     MACHINE=Linux;;
    Darwin*)    MACHINE=Mac;;
    *)          echo "不支持的操作系统: ${OS}"; exit 1;;
esac

echo "检测到系统: ${MACHINE}"
echo ""

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

# 检查命令是否存在
check_command() {
    if command -v $1 &> /dev/null; then
        echo -e "${GREEN}√${NC} $1 已安装"
        return 0
    else
        echo -e "${RED}✗${NC} $1 未安装"
        return 1
    fi
}

# 检查 Java
echo "检查 Java..."
if check_command java; then
    JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2)
    echo "  版本: $JAVA_VERSION"
else
    echo -e "${YELLOW}提示: 请先安装 Java 17+${NC}"
fi

# 检查 Maven
echo ""
echo "检查 Maven..."
check_command mvn

# 检查 Node.js
echo ""
echo "检查 Node.js..."
if check_command node; then
    NODE_VERSION=$(node -v)
    echo "  版本: $NODE_VERSION"
else
    echo -e "${YELLOW}提示: 请先安装 Node.js 16+${NC}"
fi

# 检查 npm
echo ""
echo "检查 npm..."
check_command npm

# 检查 MySQL
echo ""
echo "检查 MySQL..."
if command -v mysql &> /dev/null; then
    MYSQL_VERSION=$(mysql --version)
    echo -e "${GREEN}√${NC} MySQL 已安装 ($MYSQL_VERSION)"
else
    echo -e "${RED}✗${NC} MySQL 未安装"
fi

# 检查 Redis
echo ""
echo "检查 Redis..."
if command -v redis-server &> /dev/null || command -v redis-cli &> /dev/null; then
    REDIS_VERSION=$(redis-server --version 2>/dev/null | head -n 1)
    echo -e "${GREEN}√${NC} Redis 已安装 (${REDIS_VERSION:-server})"
else
    echo -e "${RED}✗${NC} Redis 未安装"
fi

echo ""
echo "========================================="
echo "  环境检查完成"
echo "========================================="
echo ""

# 询问是否安装缺失的软件
if [ "${MACHINE}" = "Mac" ]; then
    echo -e "${YELLOW}macOS 用户可以使用以下命令安装缺失的软件:${NC}"
    echo "  brew install openjdk@17 maven node mysql redis"
    echo ""
    read -p "是否需要我为您生成安装命令？(y/n): " -n 1 -r
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        echo ""
        echo "安装命令："
        echo "--------------------"
        echo "# 安装 Java 17"
        echo "brew install openjdk@17"
        echo ""
        echo "# 安装 Maven"
        echo "brew install maven"
        echo ""
        echo "# 安装 Node.js"
        echo "brew install node"
        echo ""
        echo "# 安装 MySQL"
        echo "brew install mysql"
        echo ""
        echo "# 安装 Redis"
        echo "brew install redis"
        echo "--------------------"
    fi
elif [ "${MACHINE}" = "Linux" ]; then
    echo -e "${YELLOW}Linux 用户请使用系统包管理器安装:${NC}"
    echo "  Ubuntu/Debian: apt-get install openjdk-17-jdk maven nodejs npm mysql-server redis-server"
    echo "  CentOS/RHEL: yum install java-17-openjdk maven nodejs npm mariadb-server redis"
fi

echo ""
read -p "是否继续配置数据库和Redis？(y/n): " -n 1 -r
if [[ ! $REPLY =~ ^[Yy]$ ]]; then
    echo "配置已取消"
    exit 0
fi

# 配置MySQL
echo ""
echo "========================================="
echo "  配置 MySQL"
echo "========================================="

MYSQL_ROOT_PASSWORD="root"
read -p "请输入MySQL root密码 (默认: root): " -r
if [ -n "$REPLY" ]; then
    MYSQL_ROOT_PASSWORD=$REPLY
fi

# 创建数据库
echo "创建数据库..."
mysql -u root -p${MYSQL_ROOT_PASSWORD} <<EOF
CREATE DATABASE IF NOT EXISTS healthy_diet DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER IF NOT EXISTS 'healthy_user'@'localhost' IDENTIFIED BY 'healthy123';
GRANT ALL PRIVILEGES ON healthy_diet.* TO 'healthy_user'@'localhost';
FLUSH PRIVILEGES;
EOF

if [ $? -eq 0 ]; then
    echo -e "${GREEN}√${NC} 数据库配置成功"
else
    echo -e "${RED}✗${NC} 数据库配置失败，请检查MySQL状态和密码"
fi

# 导入SQL脚本
echo ""
read -p "是否导入初始数据？(y/n): " -n 1 -r
if [[ $REPLY =~ ^[Yy]$ ]]; then
    SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
    SQL_FILE="$SCRIPT_DIR/backend/src/main/resources/db/migration/init.sql"

    if [ -f "$SQL_FILE" ]; then
        echo "导入SQL文件..."
        mysql -u healthy_user -phealthy123 healthy_diet < "$SQL_FILE"
        if [ $? -eq 0 ]; then
            echo -e "${GREEN}√${NC} SQL导入成功"
        else
            echo -e "${RED}✗${NC} SQL导入失败"
        fi
    else
        echo -e "${YELLOW}SQL文件不存在: $SQL_FILE${NC}"
    fi
fi

# 配置Redis
echo ""
echo "========================================="
echo "  配置 Redis"
echo "========================================="

REDIS_PASSWORD=""
read -p "Redis密码 (无密码直接回车): " -r
REDIS_PASSWORD=$REPLY

echo "启动 Redis..."

# 检测Redis启动方式
if [ "${MACHINE}" = "Mac" ]; then
    brew services start redis 2>/dev/null || redis-server --daemonize yes
elif [ "${MACHINE}" = "Linux" ]; then
    if command -v systemctl &> /dev/null; then
        sudo systemctl start redis-server || sudo systemctl start redis
    else
        redis-server --daemonize yes
    fi
fi

sleep 2

if redis-cli ping &> /dev/null; then
    echo -e "${GREEN}√${NC} Redis 启动成功"
else
    echo -e "${YELLOW}⚠${NC}  Redis 启动可能有问题，请手动检查"
fi

# 生成application.yml配置
echo ""
echo "========================================="
echo "  生成配置文件"
echo "========================================="

CONFIG_FILE="backend/src/main/resources/application.yml"

if [ -f "$CONFIG_FILE" ]; then
    cp "$CONFIG_FILE" "${CONFIG_FILE}.backup"
    echo "已备份原配置文件为 ${CONFIG_FILE}.backup"
fi

# 创建新的配置文件
cat > "$CONFIG_FILE" <<'EOF'
spring:
  application:
    name: healthy-diet-backend

  # 数据源配置
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/healthy_diet?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
    username: healthy_user
    password: healthy123

  # Redis配置
  data:
    redis:
      host: localhost
      port: 6379
EOF

if [ -n "$REDIS_PASSWORD" ]; then
    echo "      password: $REDIS_PASSWORD" >> "$CONFIG_FILE"
else
    echo "      password: \"\"" >> "$CONFIG_FILE"
fi

cat >> "$CONFIG_FILE" <<'EOF'
      database: 0
      lettuce:
        pool:
          max-active: 8
          max-wait: -1ms
          max-idle: 8
          min-idle: 0

  # 文件上传配置
  servlet:
    multipart:
      max-file-size: 10MB
      max-request-size: 10MB

  # Jackson配置
  jackson:
    time-zone: GMT+8
    date-format: yyyy-MM-dd HH:mm:ss

# MyBatis Plus配置
mybatis-plus:
  mapper-locations: classpath*:/mapper/**/*.xml
  type-aliases-package: com.healthydiet.entity
  configuration:
    map-underscore-to-camel-case: true
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
  global-config:
    db-config:
      id-type: auto
      logic-delete-field: deleted
      logic-delete-value: 1
      logic-not-delete-value: 0

# 服务器配置
server:
  port: 8080
  servlet:
    context-path: /api

# JWT配置
jwt:
  secret: healthy-diet-secret-key-2024-change-in-production
  expiration: 604800000 # 7天

# AI配置
ai:
  api-url: https://api.openai.com/v1/chat/completions
  api-key: your-api-key-here
  model: gpt-3.5-turbo

  # 图像识别配置
  image-recognition:
    enabled: true
    timeout: 30000

# 文件存储配置
file:
  upload-path: ./uploads/
  access-url: http://localhost:8080/api/file/

# 推荐算法配置
recommendation:
  enabled: true
  collaborative-filtering:
    min-interactions: 5
    similar-users: 10

# 日志配置
logging:
  level:
    com.healthydiet: debug
    org.springframework.security: debug
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"

# Knife4j配置
knife4j:
  enable: true
  openapi:
    title: 健康饮食管理系统API
    description: 基于AI的健康饮食管理小程序后端接口文档
    version: 1.0.0
    concat: contact@healthydiet.com
  setting:
    language: zh_cn
EOF

echo -e "${GREEN}√${NC} 配置文件已生成: $CONFIG_FILE"

# 创建上传目录
mkdir -p ./uploads/avatar ./uploads/analysis
echo "已创建上传目录"

echo ""
echo "========================================="
echo "  配置完成！"
echo "========================================="
echo ""
echo "后续步骤："
echo ""
echo "1. 安装后端依赖："
echo "   cd backend && mvn clean install"
echo ""
echo "2. 启动后端服务："
echo "   cd backend && mvn spring-boot:run"
echo ""
echo "3. 安装前端依赖："
echo "   cd frontend && npm install"
echo ""
echo "4. 启动前端服务："
echo "   cd frontend && npm run dev:h5"
echo ""
echo "5. 访问API文档："
echo "   http://localhost:8080/api/doc.html"
echo ""
echo "数据库信息："
echo "   数据库: healthy_diet"
echo "   用户名: healthy_user"
echo "   密码: healthy123"
echo ""
echo "Redis信息："
echo "   地址: localhost:6379"
if [ -n "$REDIS_PASSWORD" ]; then
    echo "   密码: $REDIS_PASSWORD"
fi

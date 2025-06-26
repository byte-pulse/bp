# set-java-env.ps1
# 定义所有 JDK 的路径
$jdkPaths = @{
    "JAVA8_HOME" = "C:\environment\java\jdk\jdk8"
    "JAVA11_HOME" = "C:\environment\java\jdk\jdk11"
    "JAVA17_HOME" = "C:\environment\java\jdk\jdk17"
    "JAVA21_HOME" = "C:\environment\java\jdk\jdk21"
    "JAVA25_HOME" = "C:\environment\java\jdk\jdk25"
}

# 遍历并设置为当前会话的环境变量
foreach ($key in $jdkPaths.Keys)
{
    $value = $jdkPaths[$key]
    # 检查路径是否存在
    if (Test-Path $value)
    {
        Set-Item -Path "Env:$key" -Value $value
        Write-Host "✅ 已设置 $key = $value" -ForegroundColor Green
    }
    else
    {
        Write-Host "⚠️  警告: $key 路径不存在: $value" -ForegroundColor Yellow
    }
}

mvn clean install

Write-Host "`n=== 列出所有发现的 JDK ===" -ForegroundColor Cyan
mvn org.apache.maven.plugins:maven-toolchains-plugin:3.2.0:display-discovered-jdk-toolchains
# 📦 Product Tracking

Bu proje, ürün takibi yapmak için geliştirilen bir **Spring Boot** uygulamasıdır. Kullanıcıların ürünleri ekleyip güncelleyebileceği bir sistem sağlar.
---
📌 Daha fazla detay için [GitHub Gist dokümantasyonuna buradan ulaşabilirsiniz](https://gist.github.com/kubraayazici/7fd2cea2c1d3ac39c64df9073b60a760).
## 📥 1️⃣ Projeyi Klonlayın

Projeyi kendi bilgisayarınıza almak için aşağıdaki komutu kullanın:

```sh
git clone https://github.com/kubraayazici/Product-Tracking-.git
cd Product-Tracking-
```

# 📦 2️⃣ Gerekli Bağımlılıkları Yükleyin

Spring Boot bağımlılıklarını yüklemek için:
```sh
mvn clean install  # Maven ile bağımlılıkları yükler
```
## 🛠 3️⃣ Docker Image Oluşturun ve Çalıştırın
Uygulamayı çalıştırmak için öncelikle Docker image oluşturmalısınız.
```sh
docker build -t producttracking-backend:latest .
```
Eğer Kind kullanıyorsanız, Docker image’ını Kubernetes cluster’ına yüklemek için aşağıdaki komutu çalıştırın:
```sh
kind load docker-image producttracking-backend:latest --name product-tracking
```
Bu komut, Kind içinde çalışan Kubernetes cluster’ına Docker image’ınızı ekler. Böylece Kubernetes, image’ı Docker Hub’a push etmeden doğrudan kullanabilir.
📌 Önemli Not: Eğer Kind kullanmıyorsanız, image’ı bir Docker registry’ye push etmeniz gerekebilir:
```sh
docker tag producttracking-backend:latest <your-docker-repo>/producttracking-backend:latest
docker push <your-docker-repo>/producttracking-backend:latest
```
## 🚀 4️⃣ Kubernetes Deployment
Docker image oluşturulduktan sonra uygulamayı Kubernetes ortamında çalıştırmak için aşağıdaki komutu kullanabilirsiniz:
```sh
kubectl apply -f deployment/
```
✅ Bu komut, deployment/ klasörü içindeki tüm .yaml dosyalarını okuyarak Kubernetes’e uygular.
✅ Bu komutlar, Kubernetes içinde uygulamanın çalışmasını sağlayacaktır. Pod’ların doğru çalıştığını görmek için:
```sh
kubectl get pods
kubectl get svc
kubectl get deployments
```

## 🔄 Deployment’ı Yeniden Başlatma

Eğer değişiklik yaptıysanız veya yeni Docker image’ını kullanmak istiyorsanız, Deployment’ı yeniden başlatın:
```sh
kubectl rollout restart deployment producttracking-backend
```
Bu komut, pod’ları sıfırdan oluşturarak yeni değişikliklerin devreye alınmasını sağlar.
## 🚀 5️⃣ Kubernetes Port Forwarding (API Testi için)

Uygulamayı Kubernetes ortamında çalıştırdıktan sonra, **lokal makineniz üzerinden erişebilmek için** aşağıdaki komutu çalıştırın:

```sh
kubectl port-forward svc/producttracking-backend 8080:8080
```
Bu komut, Kubernetes içindeki producttracking-backend servisini kendi bilgisayarınızın 8080 portuna yönlendirir.


## 🚀 **Doğrudan Uygulamayı Kubernetes’te Çalıştırmak için**
Eğer uygulamayı **Kubernetes içinde** çalıştırıyorsanız, doğrudan pod içinden PostgreSQL'e bağlanabilirsiniz:

```sh
kubectl exec -it <producttracking-backend-pod-adı> -- psql -h producttracking-db-primary.default.svc.cluster.local -U product_user -d product_tracking
```

Eğer lokal makineden bağlanmak istiyorsanız, Port Forward yaparak PostgreSQL’e erişebilirsiniz:
```sh
kubectl port-forward svc/producttracking-db-primary 5432:5432
```
Ve ardından lokal PostgreSQL istemcisiyle bağlanabilirsiniz:
```sh
psql -h localhost -U product_user -d product_tracking
```
## 🔑 6️⃣ Authentication & Authorization

Bu projede JWT (JSON Web Token) kullanılarak kimlik doğrulama yapılmaktadır.

Aşağıdaki adımları takip ederek giriş yapabilir, JWT token alabilir ve API’leri test edebilirsiniz.

### 📌 1️⃣ Kullanıcı Girişi (Login)

📌 /api/auth/login endpoint’i üzerinden kullanıcı giriş yapabilir ve JWT token alabilir.

```sh
curl -X POST http://localhost:8080/api/auth/login \
     -H "Content-Type: application/json" \
     -d '{
           "email": "test@example.com",
           "password": "testpassword"
         }'
```
✅ Başarılı Yanıt (200 OK):
Eğer giriş bilgileri doğruysa, JWT token içeren bir JSON dönecektir:
```sh
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```
📌 Not:
•	Dönen token’ı, yetkilendirme gerektiren API çağrılarında kullanmalısınız.
•	Token’ın geçerli süresi JWT ayarlarına bağlıdır.
•	Kullanıcının active: true olup olmadığı kontrol edilecektir.

### 📌 2️⃣ Kullanıcı Çıkış Yapma (Logout)

📌 /api/auth/logout endpoint’i ile kullanıcının çıkış yapmasını sağlayabilirsiniz.

```sh
curl -X POST http://localhost:8080/api/auth/logout \
     -H "Content-Type: application/json" \
     -d '{
           "email": "test@example.com"
         }'
```
✅ Başarılı Yanıt (200 OK):
```sh
"Logout successful."
```

📌 Not:
•	Çıkış yapan kullanıcı artık JWT token’ını kullanamaz.
•	AuthService içerisinde logout işleminin nasıl yönetildiğini (örn. token invalidation) kontrol edin.

### 📌 3️⃣ Yetkilendirme (Authorization)

📌 Bazı endpoint’lere erişebilmek için kullanıcı giriş yapmış olmalı ve active: true olmalıdır.

JWT token ile API çağrıları yapmak için Authorization header’a “Bearer Token” eklemelisiniz.

📌 Yetkilendirilmiş API Çağrısı:

```sh
curl -X GET http://localhost:8080/api/products \
     -H "Authorization: Bearer <JWT_TOKEN>"
```

🚨 Eğer yetkisiz bir istek gönderirseniz:
•	401 Unauthorized: Token geçersiz veya eksik.
•	403 Forbidden: Kullanıcının yetkisi yok.

📌 Önemli Not:
•	Yetkilendirme seviyeleri SecurityConfig.java içinde belirlenmiştir.
•	Sadece giriş yapmış ve active: true olan kullanıcılar belirli endpoint’lere ulaşabilir.
•	Eğer kullanıcı active: false ise, istek 403 Forbidden hatası dönecektir.

## 🚀 7️⃣ Postman Collection & Test Scripts

API'yi kolayca test edebilmek için **Postman Collection** dosyası ekledik.  
Bu dosyayı Postman’e **import** ederek **tüm API’leri hızlıca test edebilirsiniz**.

📌 **Postman Collection’ı indirmek için:**  
👉 [Product Tracking Postman Collection](product_tracking_postman.json)

### **📌 Postman Ortam Değişkenleri:**
Postman'de **Environment Variables** kullanarak API çağrılarını kolaylaştırabilirsiniz:

| Değişken Adı | Değer |
|--------------|----------------------|
| `{{base_url}}` | `http://localhost:8080` |
| `{{auth_token}}` | `<JWT_TOKEN>` |

📌 **Token’ı aldıktan sonra `{{auth_token}}` değişkenini güncelleyerek API çağrılarında otomatik kullanabilirsiniz.**

## 🔧 Veritabanı Bağlantısı

Bu proje **Kubernetes ortamında çalışan bir PostgreSQL servisine** bağlanacak şekilde yapılandırılmıştır.  
Aşağıdaki `application.properties` dosyasını **olduğu gibi kullanmalısınız**, herhangi bir değişiklik yapmanıza gerek yoktur:
📌 **Önemli Not:**
- **Bu PostgreSQL servisi sadece Kubernetes içindeki uygulamalar tarafından erişilebilir.**
- Eğer projeyi **lokal makinenizde çalıştırmak istiyorsanız, Kubernetes içinde çalışan bir backend pod’una bağlanmalısınız.**
- Veritabanı **`producttracking-db-primary.default.svc.cluster.local`** adresinde çalışmaktadır. **Bunu değiştirmeyin.**

---
spring.datasource.url=jdbc:postgresql://producttracking-db-primary.default.svc.cluster.local:5432/product_tracking?ssl=false
spring.datasource.username=product_user
spring.datasource.password=password123
---
PostgreSQL Driver

spring.datasource.driver-class-name=org.postgresql.Driver

Hibernate ve JPA Ayarları

spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

Connection Pool Ayarları

spring.datasource.hikari.maximum-pool-size=10
spring.datasource.hikari.minimum-idle=2
spring.datasource.hikari.idle-timeout=30000
spring.datasource.hikari.connection-timeout=20000
spring.datasource.hikari.max-lifetime=180000
spring.datasource.hikari.leakDetectionThreshold=20000
spring.datasource.hikari.validationTimeout=5000

Actuator ve Monitoring Ayarları

management.metrics.binders.processor.enabled=false
management.metrics.binders.tomcat.enabled=false
management.endpoints.web.exposure.include=*
management.endpoint.health.show-details=always


spring.datasource.url=jdbc:postgresql://producttracking-db-primary.default.svc.cluster.local:5432/product_tracking?ssl=false
spring.datasource.username=product_user
spring.datasource.password=password123

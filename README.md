Trafik Işığı Simülasyonu

Genel Bakış

Trafik Işığı Simülasyonu, JavaFX tabanlı bir uygulamadır ve dört yönlü bir kavşakta dinamik trafik ışığı kontrolünü simüle eder. Simülasyon, Kuzey, Güney, Doğu ve Batı yönlerinde araç hareketlerini (araba, kamyon ve ambulans) modelleyerek trafik ışıklarının yeşil sürelerini araç yoğunluğuna göre ayarlar. Uygulama, görselleştirme için bir tuval (canvas), araç sayıları için giriş alanları ve simülasyonu yönetmek için kontrol düğmeleri içeren bir grafik kullanıcı arayüzü (GUI) sunar.

Proje, modülerlik ve sürdürülebilirlik sağlamak için Model-View-Controller (MVC) tasarım desenini kullanır. Gerçekçi bir simülasyon ortamı oluşturmak için yollar, yaya geçitleri, evler, ağaçlar, çiçekler, ördekler ve inekler gibi görsel unsurlar içerir.

Özellikler
Dinamik Trafik Işığı Sistemi: Her yöndeki (Kuzey, Güney, Doğu, Batı) araç yoğunluğuna göre yeşil ışık sürelerini ayarlar.
Araç Türleri: Araba, kamyon ve ambulansları farklı görsel temsillerle destekler.
Çarpışma Önleme: Araçların çarpışmasını önlemek için minimum mesafe kontrolü yapar.
Etkileşimli GUI: Kullanıcıların araç sayılarını girmesine, simülasyonu başlatmasına/durdurmasına/devam ettirmesine/sıfırlamasına ve rastgele araç sayıları oluşturmasına olanak tanır.
Görsel Unsurlar: Yollar, yaya geçitleri, trafik ışıkları, evler, ağaçlar, çiçekler, ördekler ve ineklerle gerçekçi bir simülasyon ortamı sunar.
Gerçek Zamanlı Güncellemeler: Her yön için kalan yeşil ışık süresini gösterir ve araç konumlarını dinamik olarak günceller.

Gereksinimler

Projeyi çalıştırmak için aşağıdaki gereksinimlere ihtiyaç vardır:
Java Development Kit (JDK): Sürüm 11 veya üstü.
JavaFX SDK: Sürüm 11 veya üstü (Gluon adresinden indirilebilir).
IDE: IntelliJ IDEA, Eclipse veya JavaFX destekli herhangi bir IDE.
Maven/Gradle (isteğe bağlı): Bağımlılık yönetimi için, JavaFX'i bir yapı aracıyla kurmayı tercih ederseniz.


Kurulum Talimatları

*Depoyu Klonlayın:

git clone <depo-url'si>
cd trafik-isigi-simulasyonu

*JavaFX'i Yapılandırın:

JavaFX SDK'yı Gluon adresinden indirin ve kurun.
IDE'nizde veya proje yapılandırmanızda JavaFX SDK'yı ayarlayın. Örneğin, IntelliJ IDEA'da:
File > Project Structure > Libraries menüsüne gidin.
JavaFX SDK'nın lib klasörünü kütüphane olarak ekleyin.
Run > Edit Configurations bölümünde VM seçeneklerini yapılandırın:
--module-path /javafx-sdk-yolu/lib --add-modules javafx.controls,javafx.fxml


Proje Yapısı: Projenizde aşağıdaki paket yapısının olduğundan emin olun:

com.example.traffic6
├── MainApp.java
├── controller
│   └── TrafficController.java
├── model
│   ├── TrafficModel.java
│   └── Vehicle.java
├── view
│   └── TrafficView.java


Derleyin ve Çalıştırın:

Projeyi IDE'nizde açın.
Ana sınıf olarak MainApp.java'yı ayarlayın.
Uygulamayı çalıştırın. JavaFX VM seçeneklerinin doğru yapılandırıldığından emin olun.

Kullanım

Uygulamayı Başlatın: MainApp.java'yı çalıştırarak simülasyonu başlatın. "TRAFFIC LIGHT SIMULATION" başlıklı bir pencere açılacaktır.
Araç Sayılarını Girin:
Her yön (Güney, Kuzey, Batı, Doğu) için araç sayılarını ilgili metin alanlarına girin.
Yalnızca sayısal girişlere izin verilir (sayısal olmayan girişler otomatik olarak filtrelenir).


Simülasyonu Kontrol Edin:

Başlat: Girilen veya rastgele araç sayılarıyla simülasyonu başlatır.
Duraklat: Simülasyonu geçici olarak durdurur.
Devam Et: Duraklatılmış simülasyona devam eder.
Sıfırla: Tüm araçları temizler ve simülasyonu başlangıç durumuna sıfırlar.
Rastgele: Her yön için rastgele araç sayıları (0–100 araç) oluşturur ve simülasyonu günceller.


Simülasyon Detayları:

Tuval, dört yönlü bir kavşağı, yolları, trafik ışıklarını ve araçları gösterir.
Trafik ışıkları yeşil, sarı ve kırmızı fazlar arasında geçiş yapar; yeşil süreleri araç yoğunluğuna oranla ayarlanır (minimum 10 saniye, maksimum 60 saniye).
Araçlar yalnızca yeşil fazlarda hareket eder ve çarpışmayı önlemek için güvenli mesafeler korunur.
Zamanlayıcı etiketleri, her yön için kalan yeşil süreyi gösterir.

Kod Yapısı

Proje, MVC mimarisini kullanır:

Model (TrafficModel, Vehicle):
*TrafficModel: Trafik ışığı fazlarını, araç sayılarını ve yeşil sürelerini yönetir. Simülasyon mantığını, araç hareketlerini ve çarpışma önlemeyi işler.

*Vehicle: Yön, konum ve tür (araba, kamyon, ambulans) gibi özelliklere sahip bir aracı temsil eder.

View (TrafficView):
Simülasyonu JavaFX tuvalinde görselleştirir; yollar, trafik ışıkları, araçlar ve dekoratif unsurlar (evler, ağaçlar, çiçekler, ördekler, inekler) içerir.
Giriş alanları, kontrol düğmeleri ve zamanlayıcı etiketleriyle bir GUI sağlar.

Controller (TrafficController):
Model ve görünüm arasında koordinasyonu sağlar, kullanıcı girişlerini işler ve simülasyon durumunu günceller.

Main (MainApp):
MVC bileşenlerini başlatır ve JavaFX uygulamasını çalıştırır.

Bilinen Sınırlamalar
Araç Dönüşleri: Şu anda yalnızca düz hareket ("straight" turn tipi) desteklenmektedir. Sağ ve sol dönüşler tanımlı ancak uygulanmamış.

Giriş Doğrulama: Sayısal olmayan girişler filtrelenir, ancak geçersiz girişler hata mesajı yerine rastgele araç sayılarıyla çalışır.

Yaya Geçidi: Sadece Batı yönünde bir yaya geçidi uygulanmıştır. Diğer yönler için ek yaya geçitleri eklenebilir.

Performans: Yüksek araç sayıları (ör. yön başına >100) çarpışma tespit hesaplamaları nedeniyle simülasyonu yavaşlatabilir.


Gelecekteki İyileştirmeler

Sağ ve sol araç dönüşlerini akıcı animasyonlarla uygulama.
Geçersiz girişler için hata mesajları ekleme.
Daha gelişmiş çarpışma tespit algoritmaları kullanma.
Yaya simülasyonu ve yaya geçidi etkileşimleri ekleme.
Ambulanslar için trafik ışığı önceliği uygulama.
Çok sayıda araç için performansı optimize etme.


GUI ve tuval görselleştirme için JavaFX kullanıldı.

Gerçek dünya trafik ışığı sistemleri ve simülasyon projelerinden ilham alındı.

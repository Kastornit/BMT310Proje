import streamlit as st
import tensorflow as tf
import numpy as np


def model_prediction(test_image):
    # Sequential model mimarisini yeniden oluşturma
    cnn = tf.keras.models.Sequential()

    # First convolutional block
    cnn.add(tf.keras.layers.Conv2D(filters=32, kernel_size=3, padding='same', activation='relu', input_shape=[128, 128, 3], name='conv2d_1'))
    cnn.add(tf.keras.layers.Conv2D(filters=32, kernel_size=3, padding='same', activation='relu', name='conv2d_2'))
    cnn.add(tf.keras.layers.MaxPool2D(pool_size=2, strides=2, name='maxpool2d_1'))

    # Second convolutional block
    cnn.add(tf.keras.layers.Conv2D(filters=64, kernel_size=3, padding='same', activation='relu', name='conv2d_3'))
    cnn.add(tf.keras.layers.Conv2D(filters=64, kernel_size=3, padding='same', activation='relu', name='conv2d_4'))
    cnn.add(tf.keras.layers.MaxPool2D(pool_size=2, strides=2, name='maxpool2d_2'))

    # Third convolutional block
    cnn.add(tf.keras.layers.Conv2D(filters=128, kernel_size=3, padding='same', activation='relu', name='conv2d_5'))
    cnn.add(tf.keras.layers.Conv2D(filters=128, kernel_size=3, padding='same', activation='relu', name='conv2d_6'))
    cnn.add(tf.keras.layers.MaxPool2D(pool_size=2, strides=2, name='maxpool2d_3'))

    # Fourth convolutional block
    cnn.add(tf.keras.layers.Conv2D(filters=256, kernel_size=3, padding='same', activation='relu', name='conv2d_7'))
    cnn.add(tf.keras.layers.Conv2D(filters=256, kernel_size=3, padding='same', activation='relu', name='conv2d_8'))
    cnn.add(tf.keras.layers.MaxPool2D(pool_size=2, strides=2, name='maxpool2d_4'))

    # Fifth convolutional block
    cnn.add(tf.keras.layers.Conv2D(filters=512, kernel_size=3, padding='same', activation='relu', name='conv2d_9'))
    cnn.add(tf.keras.layers.Conv2D(filters=512, kernel_size=3, padding='same', activation='relu', name='conv2d_10'))
    cnn.add(tf.keras.layers.MaxPool2D(pool_size=2, strides=2, name='maxpool2d_5'))
    cnn.add(tf.keras.layers.Dropout(0.25, name='dropout_1'))

    # Flatten and dense layers
    cnn.add(tf.keras.layers.Flatten(name='flatten_1'))
    cnn.add(tf.keras.layers.Dense(units=1500, activation='relu', name='dense_1'))
    cnn.add(tf.keras.layers.Dropout(0.4, name='dropout_2'))  # To avoid overfitting

    # Output layer
    cnn.add(tf.keras.layers.Dense(units=38, activation='softmax', name='dense_2'))

    # Model ağırlıklarını yükleme
    cnn.load_weights("trained_plant_disease_model_weights.h5")

    # Modeli derleme
    cnn.compile(optimizer=tf.keras.optimizers.Adam(learning_rate=0.0001), loss='categorical_crossentropy', metrics=['accuracy'])

    # Görüntüyü yükleme ve ön işleme
    image = tf.keras.preprocessing.image.load_img(test_image, target_size=(128, 128))
    input_arr = tf.keras.preprocessing.image.img_to_array(image)
    input_arr = np.array([input_arr])  # Modelin beklediği formata getir

    # Tahmin yapma
    predictions = cnn.predict(input_arr)
    return np.argmax(predictions)


# Yan Menü
st.sidebar.title("Panel")
app_mode = st.sidebar.selectbox("Sayfa Seçin", ["Ana Sayfa", "Hakkında", "Hastalık Tanı"])

# Ana Sayfa
if app_mode == "Ana Sayfa":
    st.header("Bitki Hastalık Tanıma Sistemi")
    image_path = "images/home_page.jpeg"
    st.image(image_path, use_column_width=True)
    st.markdown("""
    Bitki Hastalık Tanıma Sistemi'ne hoş geldiniz! 🌿🔍

    Misyonumuz bitki hastalıklarını etkili bir şekilde tanımlamaya yardımcı olmaktır. Bir bitkinin resmini yükleyin, sistemimiz, herhangi bir hastalık belirtisini tespit etmek için resmi analiz edecektir. Birlikte, ürünlerimizi koruyalım ve daha sağlıklı bir hasat elde edelim!

    ### Nasıl Çalışır
    1. **Resim Yükle:** **Hastalık Tanı** sayfasına gidin ve hastalık şüphesi olan bir bitkinin resmini yükleyin.
    2. **Analiz:** Sistemimiz, potansiyel hastalıkları tanımlamak için gelişmiş algoritmalar kullanarak resmi işleyecektir.
    3. **Sonuçlar:** Sonuçları ve daha fazla işlem için önerileri görüntüleyin.

    ### Neden Bizi Seçmelisiniz?
    - **Doğruluk:** Sistemimiz, doğru hastalık tespiti için son teknolojiyi kullanır.
    - **Kullanıcı Dostu:** Basit ve sezgisel arayüzle sorunsuz bir kullanıcı deneyimi sağlar.
    - **Hızlı ve Verimli:** Sonuçları saniyeler içinde alarak hızlı karar alma imkanı sunar.

    ### Başlarken
    **Hastalık Tanı** sayfasına tıklayarak bir resim yükleyin ve Bitki Hastalık Tanıma Sistemi'nin gücünü deneyimleyin!

    ### Hakkımızda
    Projemiz, ekibimiz ve hedeflerimiz hakkında daha fazla bilgi edinin **Hakkında** sayfasında.
    """)

# Hakkında Sayfası
elif app_mode == "Hakkında":
    st.header("Hakkında")
    st.markdown("""
                #### Veri Seti Hakkında
                Bu veri seti, orijinal veri setinden çevrimdışı artırma kullanılarak yeniden oluşturulmuştur. Orijinal veri seti bu github deposunda bulunabilir.
                Bu veri seti, sağlıklı ve hastalıklı ürün yapraklarının yaklaşık 87K rgb görüntüsünden oluşur ve 38 farklı sınıfa ayrılmıştır. Toplam veri seti eğitim ve doğrulama seti olarak korunan 80/20 oranında ayrılmıştır.
                Daha sonra tahmin amacıyla 33 test görüntüsü içeren yeni bir dizin oluşturulur.
                #### İçerik
                1. Eğitim (70295 görüntü)
                2. Test (33 görüntü)
                3. Doğrulama (17572 görüntü)
                """)
    st.markdown("""  
                #### CNN Özeti :
                Model: "sequential"
                _________________________________________________________________
                Layer (type)                Output Shape              Param    
                =================================================================
                conv2d (Conv2D)             (None, 128, 128, 32)      896       
                                                                                
                conv2d_1 (Conv2D)           (None, 128, 128, 32)      9248      
                                                                                
                max_pooling2d (MaxPooling2  (None, 64, 64, 32)        0         
                D)                                                              
                                                                                
                conv2d_2 (Conv2D)           (None, 64, 64, 64)        18496     
                                                                                
                conv2d_3 (Conv2D)           (None, 64, 64, 64)        36928     
                                                                                
                max_pooling2d_1 (MaxPoolin  (None, 32, 32, 64)        0         
                g2D)                                                            
                                                                                
                conv2d_4 (Conv2D)           (None, 32, 32, 128)       73856     
                                                                                
                conv2d_5 (Conv2D)           (None, 32, 32, 128)       147584    
                                                                                
                max_pooling2d_2 (MaxPoolin  (None, 16, 16, 128)       0         
                g2D)                                                            
                                                                                
                conv2d_6 (Conv2D)           (None, 16, 16, 256)       295168    
                                                                                
                conv2d_7 (Conv2D)           (None, 16, 16, 256)       590080    
                                                                                
                max_pooling2d_3 (MaxPoolin  (None, 8, 8, 256)         0         
                g2D)                                                            
                                                                                
                conv2d_8 (Conv2D)           (None, 8, 8, 512)         1180160   
                                                                                
                conv2d_9 (Conv2D)           (None, 8, 8, 512)         2359808   
                                                                                
                max_pooling2d_4 (MaxPoolin  (None, 4, 4, 512)         0         
                g2D)                                                            
                                                                                
                dropout (Dropout)           (None, 4, 4, 512)         0         
                                                                                
                flatten (Flatten)           (None, 8192)              0         
                                                                                
                dense (Dense)               (None, 1500)              12289500  
                                                                                
                dropout_1 (Dropout)         (None, 1500)              0         
                                                                                
                dense_1 (Dense)             (None, 38)                57038     
                                                                                
                =================================================================
                Total params: 17058762 (65.07 MB)
                Trainable params: 17058762 (65.07 MB)
                Non-trainable params: 0 (0.00 Byte)
                _________________________________________________________________ 
                """)
    st.markdown("""  
                #### Doğruluk Grafiği : 
                """)
    st.image("images/plot.png", use_column_width=True)
    st.markdown("""  
                #### Karmaşıklık Matrisi : 
                """)
    st.image("images/cm.png", use_column_width=True)

    st.markdown("""  
                #### Grup Üyeleri : Ozan Evgin & İlhan Alkaya
                """)
    

# Hastalık Tanıma Sayfası
elif app_mode == "Hastalık Tanı":
    st.header("Hastalık Tanı")
    test_image = st.file_uploader("Bir Resim Seçin:")
    # Tahmin Butonu
    if st.button("Tahmin Et"):
        st.write("Tahminimiz:")
        result_index = model_prediction(test_image)
        # Etiketleri Okuma
        class_name = ['Elma___Elma_çürüklüğü', 'Elma___Siyah_çürüklük', 'Elma___Meşe_elması_pası', 'Elma___sağlıklı',
                      'Yabanmersini___sağlıklı', 'Kiraz_(ekşi_dahil)___Toz_şekeri_çürüklüğü', 
                      'Kiraz_(ekşi_dahil)___sağlıklı', 'Mısır_(mısır)___Cercospora_yaprak_lekeleri_Gri_yaprak_lekesi', 
                      'Mısır_(mısır)___Ortak_pas_', 'Mısır_(mısır)___Kuzey_Yaprak_Lekesi', 'Mısır_(mısır)___sağlıklı', 
                      'Üzüm___Siyah_çürük', 'Üzüm___Esca_(Siyah_Kızamık)', 'Üzüm___Yaprak_lekesi_(Isariopsis_Yaprak_Leke)', 
                      'Üzüm___sağlıklı', 'Portakal___Haunglongbing_(Narenciye_çürüklüğü)', 'Şeftali___Bakteriyel_leke',
                      'Şeftali___sağlıklı', 'Biber,_dolmalık___Bakteriyel_leke', 'Biber,_dolmalık___sağlıklı', 
                      'Patates___Erken_leke', 'Patates___Geç_leke', 'Patates___sağlıklı', 
                      'Ahududu___sağlıklı', 'Soya_fasulyesi___sağlıklı', 'Kabak___Toz_şekeri_çürüklüğü', 
                      'Kabak___sağlıklı', 'Çilek___Yaprak_lekesi', 'Çilek___sağlıklı', 'Domates___Bakteriyel_leke',
                      'Domates___Erken_leke', 'Domates___Geç_leke', 'Domates___Yaprak_Küf',
                      'Domates___Septoria_yaprak_lekesi', 'Domates___Örümcek_akarı_İki_lekeli_örümcek_akarı',
                      'Domates___Hedef_Leke', 'Domates___Domates_Sarı_Yaprak_Kıvrılma_Virüsü', 'Domates___Domates_mozayik_virüsü',
                      'Domates___sağlıklı']
        st.success("Modelin tahmini: {}".format(class_name[result_index]))

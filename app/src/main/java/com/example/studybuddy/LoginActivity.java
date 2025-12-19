// Объявление пакета приложения
package com.example.studybuddy;

// Импорт классов Android SDK
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

// Активность входа в приложение
public class LoginActivity extends AppCompatActivity {
    // Метод onCreate вызывается при создании активности
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Вызов родительского метода onCreate
        super.onCreate(savedInstanceState);
        // Установка макета активности из XML-ресурса
        setContentView(R.layout.activity_login);

        // Инициализация поля ввода имени
        EditText etName = findViewById(R.id.etName);
        // Инициализация кнопки входа
        Button btnLogin = findViewById(R.id.btnLogin);

        // Установка обработчика клика для кнопки входа
        btnLogin.setOnClickListener(v -> {
            // Получение введенного имени с удалением пробелов
            String name = etName.getText().toString().trim();
            // Проверка, что имя не пустое
            if (!name.isEmpty()) {
                // Показать приветственное сообщение
                Toast.makeText(this, "Привет, " + name + "!", Toast.LENGTH_SHORT).show();
                // Закрытие активности
                finish();
            } else {
                // Показать сообщение об ошибке, если имя не введено
                Toast.makeText(this, "Введите имя", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

// Объявление пакета приложения
package com.example.studybuddy;

// Импорт классов Android SDK и библиотек поддержки
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

// Активность поиска партнера для учебы
public class FindPartnerActivity extends AppCompatActivity implements UserAdapter.OnUserClickListener {
    // Элементы интерфейса: выпадающие списки, кнопки, RecyclerView
    private Spinner spinnerSubject, spinnerActivity;
    private Button btnSearch, btnMyRequests;
    private RecyclerView recyclerView;
    // Тестовый список пользователей
    private List<String> fakeUsers = new ArrayList<>();
    // Адаптер для списка пользователей
    private UserAdapter adapter;
    // Имя текущего пользователя (заглушка)
    private String currentUser = "ТекущийПользователь";

    // Метод onCreate вызывается при создании активности
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Вызов родительского метода onCreate
        super.onCreate(savedInstanceState);
        // Установка макета активности из XML-ресурса
        setContentView(R.layout.activity_find_partner);

        // Инициализация тестовых пользователей
        fakeUsers.add("Иван - Химия, Лабораторная");
        fakeUsers.add("Мария - Английский, Парная работа");
        fakeUsers.add("Алексей - Физика, Проект");
        fakeUsers.add("Ольга - Математика, Подготовка к экзамену");
        fakeUsers.add("Дмитрий - Физкультура, Парная работа");
        fakeUsers.add("Анна - Химия, Лабораторная");
        fakeUsers.add("Сергей - Математика, Подготовка к экзамену");
        fakeUsers.add("Екатерина - Физкультура, Парная работа");
        fakeUsers.add("Павел - Физика, Лабораторная");

        // Инициализация пользовательского интерфейса
        initUI();
        // Инициализация тестовых запросов для текущего пользователя
        RequestManager.getInstance().initTestRequests(currentUser);
        // Проверка наличия новых запросов
        checkNewRequests();
    }

    // Метод инициализации пользовательского интерфейса
    private void initUI() {
        // Инициализация выпадающих списков
        spinnerSubject = findViewById(R.id.spinnerSubject);
        spinnerActivity = findViewById(R.id.spinnerActivity);
        // Инициализация кнопок
        btnSearch = findViewById(R.id.btnSearch);
        btnMyRequests = findViewById(R.id.btnMyRequests);
        // Инициализация RecyclerView
        recyclerView = findViewById(R.id.recyclerView);

        // Установка LinearLayoutManager для RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        // Создание адаптера с пустым списком
        adapter = new UserAdapter(new ArrayList<>(), this);
        // Установка адаптера для RecyclerView
        recyclerView.setAdapter(adapter);

        // Массивы для выпадающих списков
        String[] subjects = {"Химия", "Физика", "Английский", "Математика", "Физкультура"};
        String[] activities = {"Лабораторная", "Парная работа", "Проект", "Подготовка к экзамену"};

        // Создание адаптеров для выпадающих списков
        ArrayAdapter<String> subjectAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, subjects);
        ArrayAdapter<String> activityAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, activities);

        // Установка адаптеров для выпадающих списков
        spinnerSubject.setAdapter(subjectAdapter);
        spinnerActivity.setAdapter(activityAdapter);

        // Установка обработчика клика для кнопки поиска
        btnSearch.setOnClickListener(v -> searchPartner());
        // Установка обработчика клика для кнопки "Мои запросы"
        btnMyRequests.setOnClickListener(v -> {
            // Создание интента для перехода к активности запросов
            Intent intent = new Intent(this, MyRequestsActivity.class);
            // Запуск активности запросов
            startActivity(intent);
        });
    }

    // Метод поиска партнера по выбранным критериям
    private void searchPartner() {
        // Получение выбранного предмета
        String subject = spinnerSubject.getSelectedItem().toString();
        // Получение выбранного типа работы
        String activity = spinnerActivity.getSelectedItem().toString();

        // Создание списка для найденных пользователей
        List<String> foundUsers = new ArrayList<>();
        // Поиск пользователей, соответствующих критериям
        for (String user : fakeUsers) {
            if (user.contains(subject) && user.contains(activity)) {
                foundUsers.add(user);
            }
        }

        // Создание нового адаптера с найденными пользователями
        adapter = new UserAdapter(foundUsers, this);
        // Установка адаптера для RecyclerView
        recyclerView.setAdapter(adapter);

        // Показать сообщение в зависимости от результата поиска
        if (!foundUsers.isEmpty()) {
            Toast.makeText(this, "Коснитесь имени пользователя, чтобы отправить запрос", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Никого не найдено", Toast.LENGTH_SHORT).show();
        }
    }

    // Обработка клика по пользователю в списке
    @Override
    public void onUserClick(String userName) {
        // Извлечение имени пользователя из строки
        String toUser = userName.split(" - ")[0];
        // Получение выбранного предмета
        String subject = spinnerSubject.getSelectedItem().toString();
        // Получение выбранного типа работы
        String activity = spinnerActivity.getSelectedItem().toString();

        // Отправка запроса через менеджер запросов
        RequestManager.getInstance().sendRequest(currentUser, toUser, subject, activity);

        // Формирование сообщения об отправке запроса
        String message = "Запрос отправлен " + toUser + " для " + subject + " (" + activity + ")";
        // Показать сообщение об отправке
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

        // Обновление кнопки запросов
        updateRequestsButton();
    }

    // Проверка наличия новых запросов
    private void checkNewRequests() {
        // Проверка через менеджер запросов
        boolean hasNew = RequestManager.getInstance().hasNewRequests(currentUser);
        if (hasNew) {
            // Показать уведомление о новых запросах
            Toast.makeText(this, "У вас есть новые запросы!", Toast.LENGTH_LONG).show();
        }
        // Обновление кнопки запросов
        updateRequestsButton();
    }

    // Обновление вида кнопки "Мои запросы"
    private void updateRequestsButton() {
        // Проверка наличия новых запросов
        boolean hasNew = RequestManager.getInstance().hasNewRequests(currentUser);
        if (hasNew) {
            // Изменение текста и цвета кнопки при наличии новых запросов
            btnMyRequests.setText("Мои запросы (ЕСТЬ НОВЫЕ!)");
            btnMyRequests.setBackgroundColor(getResources().getColor(android.R.color.holo_orange_light));
        } else {
            // Возврат к стандартному виду кнопки
            btnMyRequests.setText("Мои запросы");
            btnMyRequests.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
        }
    }

    // Метод onResume вызывается при возобновлении активности
    @Override
    protected void onResume() {
        // Вызов родительского метода onResume
        super.onResume();
        // Проверка новых запросов при возобновлении активности
        checkNewRequests();
    }
}

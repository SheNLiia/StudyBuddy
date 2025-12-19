// Объявление пакета приложения
package com.example.studybuddy;

// Импорт классов Android SDK и библиотек поддержки
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

// Объявление активности для отображения запросов пользователя
public class MyRequestsActivity extends AppCompatActivity {
    // Объявление переменных для RecyclerView и текстового поля
    private RecyclerView recyclerView;
    private TextView tvEmpty;
    // Хранение имени текущего пользователя (заглушка)
    private String currentUser = "ТекущийПользователь";

    // Метод onCreate вызывается при создании активности
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Вызов родительского метода onCreate
        super.onCreate(savedInstanceState);
        // Установка макета активности из XML-ресурса
        setContentView(R.layout.activity_my_requests);

        // Инициализация RecyclerView из макета по идентификатору
        recyclerView = findViewById(R.id.recyclerViewRequests);
        // Инициализация TextView для отображения пустого состояния
        tvEmpty = findViewById(R.id.tvEmpty);
        // Инициализация кнопки "Назад"
        Button btnBack = findViewById(R.id.btnBack);

        // Установка LinearLayoutManager для RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        // Установка обработчика клика на кнопку "Назад" для закрытия активности
        btnBack.setOnClickListener(v -> finish());
        // Загрузка запросов пользователя
        loadRequests();
    }

    // Метод загрузки запросов пользователя
    private void loadRequests() {
        // Получение всех запросов для текущего пользователя из менеджера
        List<RequestManager.Request> all = RequestManager.getInstance().getRequestsForUser(currentUser);
        // Создание списков для входящих и исходящих запросов
        List<RequestManager.Request> incoming = new ArrayList<>();
        List<RequestManager.Request> outgoing = new ArrayList<>();

        // Разделение запросов на входящие и исходящие
        for (RequestManager.Request r : all) {
            // Если запрос адресован текущему пользователю, добавить во входящие
            if (r.getToUser().equals(currentUser)) {
                incoming.add(r);
                // Если запрос отправлен текущим пользователем, добавить в исходящие
            } else if (r.getFromUser().equals(currentUser)) {
                outgoing.add(r);
            }
        }

        // Проверка наличия запросов
        if (incoming.isEmpty() && outgoing.isEmpty()) {
            // Если запросов нет, показать текстовое поле с сообщением
            tvEmpty.setVisibility(View.VISIBLE);
            // Скрыть RecyclerView
            recyclerView.setVisibility(View.GONE);
            // Установка текста сообщения
            tvEmpty.setText("У вас пока нет запросов");
            // Выход из метода
            return;
        }

        // Если есть запросы, скрыть текстовое поле
        tvEmpty.setVisibility(View.GONE);
        // Показать RecyclerView
        recyclerView.setVisibility(View.VISIBLE);

        // Создание списка элементов для адаптера (заголовки + запросы)
        List<RequestManager.RequestItem> items = new ArrayList<>();

        // Добавление заголовка и элементов для входящих запросов
        if (!incoming.isEmpty()) {
            // Добавление заголовка раздела
            items.add(new RequestManager.RequestItem("Запросы мне"));
            // Добавление каждого входящего запроса
            for (RequestManager.Request r : incoming) {
                items.add(new RequestManager.RequestItem(r));
            }
        }

        // Добавление заголовка и элементов для исходящих запросов
        if (!outgoing.isEmpty()) {
            // Добавление заголовка раздела
            items.add(new RequestManager.RequestItem("Мои запросы другим"));
            // Добавление каждого исходящего запроса
            for (RequestManager.Request r : outgoing) {
                items.add(new RequestManager.RequestItem(r));
            }
        }

        // Создание адаптера для RecyclerView
        RequestManager.RequestAdapter adapter = new RequestManager.RequestAdapter(
                items,
                currentUser,
                // Создание слушателя действий для запросов
                new RequestManager.RequestAdapter.OnRequestActionListener() {
                    // Обработка принятия запроса
                    @Override
                    public void onAccept(RequestManager.Request request) {
                        // Вызов метода принятия запроса в менеджере
                        RequestManager.getInstance().acceptRequest(request.getId());
                        // Перезагрузка запросов для обновления интерфейса
                        loadRequests();
                    }

                    // Обработка отклонения запроса
                    @Override
                    public void onReject(RequestManager.Request request) {
                        // Вызов метода отклонения запроса в менеджере
                        RequestManager.getInstance().rejectRequest(request.getId());
                        // Перезагрузка запросов для обновления интерфейса
                        loadRequests();
                    }
                });

        // Установка адаптера для RecyclerView
        recyclerView.setAdapter(adapter);
    }
}
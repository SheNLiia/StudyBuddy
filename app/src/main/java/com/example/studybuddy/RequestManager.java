// Объявление пакета приложения
package com.example.studybuddy;

// Импорт классов Android SDK и библиотек поддержки
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

// Менеджер для управления запросами между пользователями
public class RequestManager {
    // Статическая переменная для хранения единственного экземпляра (Singleton)
    private static RequestManager instance;
    // Карта для хранения запросов по пользователям
    private Map<String, List<Request>> userRequests = new HashMap<>();
    // Общий список всех запросов
    private List<Request> allRequests = new ArrayList<>();
    // Счетчик для генерации уникальных ID запросов
    private int requestCounter = 1;

    // Приватный конструктор (паттерн Singleton)
    private RequestManager() {}

    // Метод для получения единственного экземпляра менеджера
    public static RequestManager getInstance() {
        // Если экземпляр не создан, создать новый
        if (instance == null) {
            instance = new RequestManager();
        }
        // Возврат экземпляра
        return instance;
    }

    // Инициализация тестовых запросов для пользователя
    public void initTestRequests(String currentUser) {
        // Проверка, есть ли уже запросы у пользователя
        if (userRequests.containsKey(currentUser) && !userRequests.get(currentUser).isEmpty()) {
            // Если есть, выйти из метода
            return;
        }

        // Создание тестовых запросов для пользователя
        sendRequest("Иван", currentUser, "Математика", "Подготовка к экзамену");
        sendRequest("Мария", currentUser, "Английский", "Парная работа");
    }

    // Метод отправки запроса
    public void sendRequest(String fromUser, String toUser, String subject, String activity) {
        // Создание нового объекта запроса
        Request request = new Request(fromUser, toUser, subject, activity);
        // Установка уникального ID
        request.setId("req_" + requestCounter++);

        // Добавление запроса в общий список
        allRequests.add(request);
        // Добавление запроса в список для получателя
        userRequests.computeIfAbsent(toUser, k -> new ArrayList<>()).add(request);
        // Добавление запроса в список для отправителя
        userRequests.computeIfAbsent(fromUser, k -> new ArrayList<>()).add(request);
    }

    // Получение всех запросов для конкретного пользователя
    public List<Request> getRequestsForUser(String userName) {
        // Получение списка запросов пользователя или пустого списка
        List<Request> requests = userRequests.get(userName);
        return requests != null ? requests : new ArrayList<>();
    }

    // Метод принятия запроса
    public void acceptRequest(String requestId) {
        // Поиск запроса по ID
        for (Request request : allRequests) {
            if (request.getId().equals(requestId)) {
                // Установка статуса "accepted"
                request.setStatus("accepted");
                break;
            }
        }
    }

    // Метод отклонения запроса
    public void rejectRequest(String requestId) {
        // Поиск запроса по ID
        for (Request request : allRequests) {
            if (request.getId().equals(requestId)) {
                // Установка статуса "rejected"
                request.setStatus("rejected");
                break;
            }
        }
    }

    // Проверка наличия новых (ожидающих) запросов у пользователя
    public boolean hasNewRequests(String userName) {
        // Получение всех запросов пользователя
        List<Request> requests = getRequestsForUser(userName);
        // Поиск запросов со статусом "pending"
        for (Request request : requests) {
            if ("pending".equals(request.getStatus())) {
                return true;
            }
        }
        return false;
    }

    // ==================== Внутренние классы ====================

    // Класс, представляющий запрос
    public static class Request {
        // Поля запроса
        private String id;
        private String fromUser;
        private String toUser;
        private String subject;
        private String activity;
        private String status;
        private long timestamp;

        // Конструктор по умолчанию
        public Request() {}

        // Конструктор с параметрами
        public Request(String fromUser, String toUser, String subject, String activity) {
            this.fromUser = fromUser;
            this.toUser = toUser;
            this.subject = subject;
            this.activity = activity;
            // Установка начального статуса "pending"
            this.status = "pending";
            // Установка текущего времени как временной метки
            this.timestamp = System.currentTimeMillis();
        }

        // Геттеры и сеттеры для всех полей
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }

        public String getFromUser() { return fromUser; }
        public void setFromUser(String fromUser) { this.fromUser = fromUser; }

        public String getToUser() { return toUser; }
        public void setToUser(String toUser) { this.toUser = toUser; }

        public String getSubject() { return subject; }
        public void setSubject(String subject) { this.subject = subject; }

        public String getActivity() { return activity; }
        public void setActivity(String activity) { this.activity = activity; }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }

        public long getTimestamp() { return timestamp; }
        public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    }

    // Класс элемента списка запросов (может быть заголовком или запросом)
    public static class RequestItem {
        // Константы для типов элементов
        public static final int TYPE_HEADER = 0;
        public static final int TYPE_REQUEST = 1;

        // Поля элемента
        private int type;
        private String headerTitle;
        private Request request;

        // Конструктор для заголовка
        public RequestItem(String headerTitle) {
            this.type = TYPE_HEADER;
            this.headerTitle = headerTitle;
        }

        // Конструктор для запроса
        public RequestItem(Request request) {
            this.type = TYPE_REQUEST;
            this.request = request;
        }

        // Геттеры для полей
        public int getType() { return type; }
        public String getHeaderTitle() { return headerTitle; }
        public Request getRequest() { return request; }
    }

    // ==================== Адаптер ====================

    // Адаптер для отображения списка запросов в RecyclerView
    public static class RequestAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        // Список элементов для отображения
        private List<RequestItem> items;
        // Имя текущего пользователя
        private String currentUser;
        // Слушатель действий для запросов
        private OnRequestActionListener listener;

        // Интерфейс для обработки действий с запросами
        public interface OnRequestActionListener {
            void onAccept(Request request);
            void onReject(Request request);
        }

        // Конструктор адаптера
        public RequestAdapter(List<RequestItem> items, String currentUser, OnRequestActionListener listener) {
            this.items = items;
            this.currentUser = currentUser;
            this.listener = listener;
        }

        // Определение типа элемента по позиции
        @Override
        public int getItemViewType(int position) {
            return items.get(position).getType();
        }

        // Получение количества элементов
        @Override
        public int getItemCount() {
            return items.size();
        }

        // ViewHolder для заголовков
        static class HeaderViewHolder extends RecyclerView.ViewHolder {
            TextView tvHeader;
            HeaderViewHolder(View itemView) {
                super(itemView);
                tvHeader = itemView.findViewById(R.id.tvHeader);
            }
        }

        // ViewHolder для запросов
        static class RequestViewHolder extends RecyclerView.ViewHolder {
            // Элементы интерфейса для отображения информации о запросе
            TextView tvFromUser, tvSubject, tvActivity, tvTime, tvStatus;
            Button btnAccept, btnReject;

            RequestViewHolder(View itemView) {
                super(itemView);
                // Инициализация всех TextView и Button из макета
                tvFromUser = itemView.findViewById(R.id.tvFromUser);
                tvSubject = itemView.findViewById(R.id.tvSubject);
                tvActivity = itemView.findViewById(R.id.tvActivity);
                tvTime = itemView.findViewById(R.id.tvTime);
                tvStatus = itemView.findViewById(R.id.tvStatus);
                btnAccept = itemView.findViewById(R.id.btnAccept);
                btnReject = itemView.findViewById(R.id.btnReject);
            }
        }

        // Создание нового ViewHolder в зависимости от типа
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            // Для заголовка
            if (viewType == RequestItem.TYPE_HEADER) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_header, parent, false);
                return new HeaderViewHolder(view);
            }

            // Для запроса
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_request, parent, false);
            return new RequestViewHolder(view);
        }

        // Привязка данных к ViewHolder
        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            // Получение элемента по позиции
            RequestItem item = items.get(position);

            // Обработка заголовка
            if (item.getType() == RequestItem.TYPE_HEADER) {
                ((HeaderViewHolder) holder).tvHeader.setText(item.getHeaderTitle());
                return;
            }

            // Получение объекта запроса
            Request request = item.getRequest();
            // Приведение holder к RequestViewHolder
            RequestViewHolder h = (RequestViewHolder) holder;

            // Заполнение данных о запросе
            if (request.getFromUser().equals(currentUser)) {
                // Если текущий пользователь отправитель
                h.tvFromUser.setText("Кому: " + request.getToUser());
            } else {
                // Если текущий пользователь получатель
                h.tvFromUser.setText("От: " + request.getFromUser());
            }

            // Заполнение информации о предмете и типе работы
            h.tvSubject.setText("Предмет: " + request.getSubject());
            h.tvActivity.setText("Работа: " + request.getActivity());

            // Форматирование и отображение времени запроса
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());
            h.tvTime.setText("Время: " + sdf.format(new Date(request.getTimestamp())));

            // Отображение статуса запроса
            h.tvStatus.setText("Статус: " + getStatusText(request.getStatus()));

            // Управление видимостью кнопок
            if ("pending".equals(request.getStatus()) && !request.getFromUser().equals(currentUser)) {
                // Показать кнопки только для входящих запросов в статусе pending
                h.btnAccept.setVisibility(View.VISIBLE);
                h.btnReject.setVisibility(View.VISIBLE);
            } else {
                // Скрыть кнопки для всех остальных случаев
                h.btnAccept.setVisibility(View.GONE);
                h.btnReject.setVisibility(View.GONE);
            }

            // Установка обработчика для кнопки принятия
            h.btnAccept.setOnClickListener(v -> {
                if (listener != null) listener.onAccept(request);
            });

            // Установка обработчика для кнопки отклонения
            h.btnReject.setOnClickListener(v -> {
                if (listener != null) listener.onReject(request);
            });
        }

        // Метод для преобразования статуса в читаемый текст
        private String getStatusText(String status) {
            switch (status) {
                case "pending": return "Ожидает ответа";
                case "accepted": return "Принят";
                case "rejected": return "Отклонен";
                default: return status;
            }
        }
    }
}
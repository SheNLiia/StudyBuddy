// Объявление пакета приложения
package com.example.studybuddy;

// Импорт классов Android SDK и библиотек поддержки
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

// Адаптер для отображения списка пользователей
public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    // Список пользователей
    private List<String> userList;
    // Слушатель кликов по пользователям
    private OnUserClickListener listener;

    // Интерфейс для обработки кликов по пользователю
    public interface OnUserClickListener {
        void onUserClick(String userName);
    }

    // Конструктор адаптера
    public UserAdapter(List<String> userList, OnUserClickListener listener) {
        // Инициализация списка пользователей
        this.userList = userList;
        // Инициализация слушателя кликов
        this.listener = listener;
    }

    // Создание нового ViewHolder
    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Создание View из стандартного макета Android
        View view = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_1, parent, false);
        // Возврат нового ViewHolder
        return new UserViewHolder(view);
    }

    // Привязка данных к ViewHolder
    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        // Получение пользователя по позиции
        String user = userList.get(position);
        // Установка текста пользователя в TextView
        holder.textView.setText(user);

        // Установка обработчика клика на элемент списка
        holder.itemView.setOnClickListener(v -> {
            // Проверка, что слушатель установлен
            if (listener != null) {
                // Вызов метода слушателя при клике
                listener.onUserClick(user);
            }
        });
    }

    // Получение количества элементов в списке
    @Override
    public int getItemCount() {
        return userList.size();
    }

    // ViewHolder для элементов пользователя
    static class UserViewHolder extends RecyclerView.ViewHolder {
        // TextView для отображения имени пользователя
        TextView textView;
        // Конструктор ViewHolder
        UserViewHolder(View itemView) {
            super(itemView);
            // Инициализация TextView из стандартного идентификатора Android
            textView = itemView.findViewById(android.R.id.text1);
        }
    }
}
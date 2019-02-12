package com.udemy.whatmedo;

/**
 * Активность должна: кликать на кнопку +,
 * выводить во фрагменте строку ввода,
 * сохранять текст в базу данных,
 * обновлять прокручивающийся список из базы,
 * при клике на элемент списка предлагать удалить его (и из базы тоже)
 */

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;


import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    //Для списка: делаем массив и адаптер
    private ArrayList<String> listItems;
    private ArrayAdapter<String> adapter;
    private ListView tasklist;

    private FragmentManager mFragmentManager;
    private FragmentTransaction mFragmentTransaction;
    private Fragment inputfragment;

    private void inflateListItems() {
        listItems = new ArrayList<>();
        listItems.add("Тестовая строка 1");
        listItems.add("Тестовая строка 2");
        listItems.add("Тестовая строка 3");
        listItems.add("Тестовая строка 4");
        listItems.add("Тестовая строка 5");
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listItems);
        tasklist = findViewById(R.id.added_tasks);
        tasklist.setAdapter(adapter);

        //Создаём слушателя для listView tasklist
        tasklist.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> listView, View itemViewClicked, int i, long l) {
                //Вызваем диалоговое окно подтверждения выбора и удаляем элемент i
                createConfirmDialog("DeleteTask", "Удаляем строку?", i);
                }
        });
    }

    // создает диалоговое окно с кнопкаи
    private void createConfirmDialog(String title, String content, final int indexToBeRemoved) {

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(title); // Название — необязательный элемент диалогового окна
        builder.setMessage(content); // Контент — текст, который будет показан пользователю. Это может быть сообщение, список или же свой полностью кастомный макет.
        builder.setNegativeButton("ОТМЕНА",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        builder.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        listItems.remove(indexToBeRemoved);
                        adapter.notifyDataSetChanged();
                    }
                });
        builder.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mFragmentManager = getFragmentManager();
        inflateListItems();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //"Надуваем" меню нашим макетом
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Получаем ID объекта, на котором кликнули, и выводим фрагмент c полем ввода
        if (item.getItemId() == R.id.record_create_order) {
            if (inputfragment == null) inputfragment = new InputFragment();

            mFragmentTransaction = mFragmentManager.beginTransaction();
            mFragmentTransaction.add(R.id.container, inputfragment, getString(R.string.ifrag_tag));
            mFragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            mFragmentTransaction.commit();

            //Установим редактируемому тексту слушателя, для этого используем handler
            //Если сразу пытаться задать слушателя, не успевает проходить транзакция
            setFragmentListener();

        }

        return super.onOptionsItemSelected(item);
    }

    //В этом методе мы назначаем слушателя для редактора ввода текста.
    //слушатель должен реагировать на done/return закрытием и передачей текста в базу
    private void setFragmentListener() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                inputfragment = mFragmentManager.findFragmentByTag(getString(R.string.ifrag_tag));
                EditText editText = inputfragment.getView().findViewById(R.id.edit_note);
                editText.setOnEditorActionListener(
                        new EditText.OnEditorActionListener() {
                            @Override
                            public boolean onEditorAction(TextView editText, int actionId, KeyEvent event) {
                                if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                                        actionId == EditorInfo.IME_ACTION_DONE ||
                                        event != null &&
                                                event.getAction() == KeyEvent.ACTION_DOWN &&
                                                event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                                    if (event == null || !event.isShiftPressed()) {
                                        //Пользователь завершил ввод, можно закрывать фрагмент
                                        //Но сначала передадим введённую строку в список
                                        listItems.add(0, editText.getText().toString());
                                        adapter.notifyDataSetChanged();
                                        editText.setText(""); //обнуляем текст фрагмента, сам фрагмент остаётся в памяти

                                        mFragmentTransaction = mFragmentManager.beginTransaction();
                                        mFragmentTransaction.remove(inputfragment);
                                        mFragmentTransaction.commit();
                                        return true; // consume.
                                    }
                                }
                                return false; // pass on to other listeners.
                            }
                        }
                );
            }
        }, 1000);
    }
}
package com.example.bookstore;

import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> {

    private ArrayList<Book> bookList;
    private ArrayList<Book> selectedBooks = new ArrayList<>();

    public BookAdapter(ArrayList<Book> bookList) {
        this.bookList = bookList;
    }

    public ArrayList<Book> getSelectedBooks() {
        return selectedBooks;
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.book_item, parent, false);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        Book book = bookList.get(position);

        holder.title.setText(book.getTitle());
        holder.isbn.setText("ISBN: " + book.getIsbn());
        holder.price.setText("$" + book.getPrice());
        holder.available.setText("Available: " + book.getQuantity());

        // prevent old TextWatcher from previous recycled views
        if (holder.currentWatcher != null) {
            holder.qty.removeTextChangedListener(holder.currentWatcher);
        }

        holder.qty.setText(String.valueOf(book.getSelectedQuantity()));
        holder.title.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), BookDetails.class);
            intent.putExtra("title", book.getTitle());
            intent.putExtra("isbn", book.getIsbn());
            intent.putExtra("price", book.getPrice());
            intent.putExtra("quantity", book.getQuantity());
            v.getContext().startActivity(intent);
        });

        // Checkbox
        holder.checkBox.setOnCheckedChangeListener(null);
        holder.checkBox.setChecked(selectedBooks.contains(book));
        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) selectedBooks.add(book);
            else selectedBooks.remove(book);
        });

        // Create new TextWatcher
        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                int qty = 1;
                try {
                    qty = Integer.parseInt(s.toString());
                    if (qty < 1) qty = 1;
                    if (qty > book.getQuantity()) qty = book.getQuantity();
                } catch (NumberFormatException e) {
                    qty = 1;
                }

                book.setSelectedQuantity(qty);
            }
        };

        holder.currentWatcher = watcher;
        holder.qty.addTextChangedListener(watcher);
    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }

    public static class BookViewHolder extends RecyclerView.ViewHolder {
        TextView title, isbn, price, available;
        EditText qty;
        CheckBox checkBox;

        TextWatcher currentWatcher; // Keeps reference to remove it properly

        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.checkBox);
            title = itemView.findViewById(R.id.textViewTitle);
            isbn = itemView.findViewById(R.id.textViewIsbn);
            price = itemView.findViewById(R.id.textViewPrice);
            available = itemView.findViewById(R.id.textAvailableQty);
            qty = itemView.findViewById(R.id.editQuantity);
        }
    }
}

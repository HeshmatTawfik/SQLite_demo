package com.heshmat.sqllitedemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements LinksAdapter.LinksAdapterListener {

    private SearchView searchView;
    LinksAdapter mAdapter;
    DatabaseAdapter mDatabaseAdapter;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    ArrayList<LinkModel> linkModels;
    static long selectedLink = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.app_name);
        linkModels = new ArrayList<>();
        mAdapter = new LinksAdapter(this, linkModels, this);
        mDatabaseAdapter = DatabaseAdapter.getDatabaseAdapter(this, mAdapter);
        mDatabaseAdapter.linkModels();
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(null);
        recyclerView.setAdapter(mAdapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                mAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                mAdapter.getFilter().filter(query);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        return super.onContextItemSelected(item);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (!searchView.isIconified()) {
            searchView.setIconified(true);
            return;
        }
        super.onBackPressed();
    }


    @Override
    public void onContactSelected(LinkModel linkModel) {

    }

    @Override
    public void onLongPress(long id, int index) {
        LinkModel linkModel = LinkModel.returnSelectedLink(id, mAdapter.getLinksList());

    }

    @Override
    public void delete(long id) {
        LinkModel linkModel = LinkModel.returnSelectedLink(id, mAdapter.getLinksList());
        mDatabaseAdapter.delete(linkModel.getId());

    }

    @Override
    public void update(long currentITemId) {
        int index=mAdapter.getLinksListFiltered().indexOf(new LinkModel(currentITemId));
        LinkModel linkModel =mAdapter.getLinksListFiltered().get(index);


        View addLinkView = LayoutInflater.from(this).inflate(R.layout.add_link_layout, null);
        TextInputLayout addLinkNameTL = addLinkView.findViewById(R.id.addLinkNameTl);
        TextInputLayout addLinkCommentTL = addLinkView.findViewById(R.id.addLinkCommentTl);
        TextInputLayout addLinkUrlTL = addLinkView.findViewById(R.id.addLinkUrlTl);
        addLinkNameTL.getEditText().setText(linkModel.getName());
        addLinkCommentTL.getEditText().setText(linkModel.getComment());
        addLinkUrlTL.getEditText().setText(linkModel.getUrl());
        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(addLinkView)
                .setTitle("Edit link")
                .setPositiveButton(android.R.string.ok, null) //Set to null. We override the onclick
                .setNegativeButton(android.R.string.cancel, null)
                .create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // TODO Do something
                        boolean isValidate = validateText(addLinkNameTL) && validateText(addLinkCommentTL) && validateText(addLinkUrlTL);
                        if (isValidate) {
                            String name = addLinkNameTL.getEditText().getText().toString().trim();
                            String comment = addLinkCommentTL.getEditText().getText().toString().trim();
                            String url = addLinkUrlTL.getEditText().getText().toString().trim();
                            LinkModel updatedLinkModel=new LinkModel(currentITemId,name,comment,url);
                            mDatabaseAdapter.update(currentITemId,updatedLinkModel);
                            dialog.dismiss();
                        }
                    }
                });
            }
        });

        dialog.show();

    }

    @OnClick(R.id.fabAddLink)
    public void fabAddALink(View view) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View addLinkView = LayoutInflater.from(this).inflate(R.layout.add_link_layout, null);
        TextInputLayout addLinkNameTL = addLinkView.findViewById(R.id.addLinkNameTl);
        TextInputLayout addLinkCommentTL = addLinkView.findViewById(R.id.addLinkCommentTl);
        TextInputLayout addLinkUrlTL = addLinkView.findViewById(R.id.addLinkUrlTl);
        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(addLinkView)
                .setTitle("Add link")
                .setPositiveButton(android.R.string.ok, null) //Set to null. We override the onclick
                .setNegativeButton(android.R.string.cancel, null)
                .create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // TODO Do something
                        boolean isValidate = validateText(addLinkNameTL) && validateText(addLinkCommentTL) && validateText(addLinkUrlTL);

                        //Dismiss once everything is OK.
                        if (isValidate) {
                            String name = addLinkNameTL.getEditText().getText().toString().trim();
                            String comment = addLinkCommentTL.getEditText().getText().toString().trim();
                            String url = addLinkUrlTL.getEditText().getText().toString().trim();

                            mDatabaseAdapter.insert(new LinkModel(name, comment, url));
                            dialog.dismiss();
                        }
                    }
                });
            }
        });
        dialog.show();


    }

    public boolean validateText(TextInputLayout textInputLayout) {
        String text = textInputLayout.getEditText().getText().toString();
        if (text.trim().isEmpty()) {
            textInputLayout.setError("Field required");
            return false;
        }
        textInputLayout.setError(null);
        return true;
    }
}

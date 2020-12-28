package com.heshmat.sqllitedemo;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


public class LinksAdapter extends RecyclerView.Adapter<LinksAdapter.MyViewHolder>
        implements Filterable {
    private Context context;
    private List<LinkModel> linksList;
    private long currentITemId = -1;

    public List<LinkModel> getLinksList() {
        return linksList;
    }

    public List<LinkModel> getLinksListFiltered() {
        return linksListFiltered;
    }

    private List<LinkModel> linksListFiltered;
    private LinksAdapterListener listener;

    public LinksAdapter(Context context, List<LinkModel> linksList, LinksAdapterListener listener) {
        this.context = context;
        this.listener = listener;
        this.linksList = linksList;
        this.linksListFiltered = linksList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.link_item_view, parent, false);

        return new MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        final LinkModel linkModel = linksListFiltered.get(position);
        holder.name.setText(linkModel.getName());
        holder.url.setText(linkModel.getUrl());
        holder.comment.setText(linkModel.getComment());

        holder.openLinkBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (linkModel.getUrl().toLowerCase().contains("facebook")) {

                    Intent facebookIntent = new Intent(Intent.ACTION_VIEW);
                    String facebookUrl = getFacebookPageURL(context, holder.url.getText().toString());
                    facebookIntent.setData(Uri.parse(facebookUrl));
                    context.startActivity(facebookIntent);
                } else if (linkModel.getUrl().toLowerCase().contains("instagram")) {
                    Uri uri = Uri.parse(linkModel.getUrl());
                    Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);

                    likeIng.setPackage("com.instagram.android");

                    try {
                        context.startActivity(likeIng);
                    } catch (ActivityNotFoundException e) {
                        context.startActivity(new Intent(Intent.ACTION_VIEW,
                                Uri.parse(linkModel.getUrl())));
                    }

                } else {
                    if (URLUtil.isValidUrl(linkModel.getUrl())) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(linkModel.getUrl()));
                        context.startActivity(browserIntent);
                    } else {
                        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/search?q=" + linkModel.getUrl())));

                    }

                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return linksListFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    linksListFiltered = linksList;
                } else {
                    List<LinkModel> filteredList = new ArrayList<>();
                    for (LinkModel row : linksList) {

                        if (row.getName().toLowerCase().contains(charString.toLowerCase()) || row.getUrl().contains(charSequence)) {
                            filteredList.add(row);
                        }
                    }

                    linksListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = linksListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                linksListFiltered = (ArrayList<LinkModel>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        public TextView name, url, comment;
        ImageView openLinkBt;

        public MyViewHolder(@NonNull View view) {
            super(view);
            view.setOnCreateContextMenuListener(this);
            name = view.findViewById(R.id.nameTv);
            url = view.findViewById(R.id.urlTv);
            comment = view.findViewById(R.id.commentTv);
            openLinkBt = view.findViewById(R.id.openLinkBt);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onContactSelected(linksListFiltered.get(getAdapterPosition()));
                }
            });
            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    currentITemId = linksListFiltered.get(getAdapterPosition()).getId();
                    listener.onLongPress(linksListFiltered.get(getAdapterPosition()).getId(), getAdapterPosition());

                    return false;
                }
            });
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.add(0, v.getId(), 0, "Update").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    listener.update(currentITemId);

                    return false;
                }
            });
            menu.add(0, v.getId(), 0, "delete").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    listener.delete(currentITemId);
                    return false;
                }
            });

        }

    }

    public interface LinksAdapterListener {
        void onContactSelected(LinkModel linkModel);

        void onLongPress(long id, int index);

        void delete(long id);

        void update(long currentITemId);
    }

    public String getFacebookPageURL(Context context, String FACEBOOK_URL) {
        PackageManager packageManager = context.getPackageManager();
        try {
            int versionCode = packageManager.getPackageInfo("com.facebook.katana", 0).versionCode;
            if (versionCode >= 3002850) { //newer versions of fb app
                return "fb://facewebmodal/f?href=" + FACEBOOK_URL;
            } else { //older versions of fb app
                return "fb://page/" + "FACEBOOK_PAGE_ID";
            }
        } catch (PackageManager.NameNotFoundException e) {
            return FACEBOOK_URL; //normal web url
        }
    }
}

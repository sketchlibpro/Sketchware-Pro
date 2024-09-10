package com.besome.sketch.editor.event;

import a.a.a.bB;
import a.a.a.dt;
import a.a.a.gB;
import a.a.a.jC;
import a.a.a.mB;
import a.a.a.oq;
import a.a.a.rs;
import a.a.a.wB;
import a.a.a.xB;

import android.animation.Animator;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.besome.sketch.beans.ComponentBean;
import com.besome.sketch.beans.EventBean;
import com.besome.sketch.beans.ProjectFileBean;
import com.besome.sketch.beans.ViewBean;
import com.besome.sketch.lib.base.BaseAppCompatActivity;
import com.google.android.gms.analytics.HitBuilders;
import com.sketchware.remod.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class AddEventActivity extends BaseAppCompatActivity implements View.OnClickListener {
    private ArrayList<EventBean> addableDrawerViewEvents;
    private ArrayList<EventBean> eventsToAdd;
    private boolean C;
    private Button add_button;
    private Button cancel_button;
    private int categoryIndex;
    private TextView empty_message;
    private dt moreBlockView;
    private String sc_id;
    private ProjectFileBean projectFile;
    private CategoryAdapter categoryAdapter;
    private EventAdapter eventAdapter;
    private EventsToAddAdapter eventsToAddAdapter;
    private TextView tv_category;
    private RecyclerView event_list;
    private RecyclerView events_preview;
    private LinearLayout container;
    private ScrollView moreblock_layout;
    private HashMap<Integer, ArrayList<EventBean>> categories;
    private ArrayList<EventBean> addableEtcEvents;
    private ArrayList<EventBean> addableViewEvents;
    private ArrayList<EventBean> addableComponentEvents;
    private ArrayList<EventBean> addableActivityEvents;

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.ani_fade_in, R.anim.ani_fade_out);
    }

    private void l() {
        if (eventsToAdd.size() == 0 && !C) {
            C = true;
            gB.a(events_preview, 300, new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(@NonNull Animator animation) {
                }

                @Override
                public void onAnimationEnd(@NonNull Animator animation) {
                    events_preview.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationCancel(@NonNull Animator animation) {
                }

                @Override
                public void onAnimationRepeat(@NonNull Animator animation) {
                }
            });
        } else if (eventsToAdd.size() > 0 && C) {
            C = false;
            events_preview.setVisibility(View.VISIBLE);
            gB.b(events_preview, 300, null);
        }
    }

    private void initialize() {
        addableEtcEvents.clear();
        addableActivityEvents.clear();
        addableComponentEvents.clear();
        addableDrawerViewEvents.clear();
        addableEtcEvents.clear();
        eventsToAdd.clear();
        String[] activityEvents = oq.a();
        int length = activityEvents.length;
        int i = 0;
        while (true) {
            boolean exists = true;
            if (i >= length) {
                break;
            }
            String activityEvent = activityEvents[i];
            Iterator<EventBean> it = jC.a(sc_id).g(projectFile.getJavaName()).iterator();
            while (true) {
                if (!it.hasNext()) {
                    exists = false;
                    break;
                }
                EventBean next = it.next();
                if (next.eventType == EventBean.EVENT_TYPE_ACTIVITY && activityEvent.equals(next.eventName)) {
                    break;
                }
            }
            if (!exists) {
                addableActivityEvents.add(new EventBean(EventBean.EVENT_TYPE_ACTIVITY, 0, activityEvent, activityEvent));
            }
            i++;
        }
        ArrayList<ViewBean> views = jC.a(sc_id).d(projectFile.getXmlName());
        ArrayList<ComponentBean> components = jC.a(sc_id).e(projectFile.getJavaName());
        if (views != null) {
            Iterator<ViewBean> it2 = views.iterator();
            while (it2.hasNext()) {
                ViewBean view = it2.next();
                String[] viewEvents = oq.c(view.getClassInfo());
                if (viewEvents != null) {
                    for (String viewEvent : viewEvents) {
                        Iterator<EventBean> existingEvents = jC.a(sc_id).g(projectFile.getJavaName()).iterator();
                        boolean viewEventExists;
                        while (true) {
                            if (!existingEvents.hasNext()) {
                                viewEventExists = false;
                                break;
                            }
                            EventBean existingEvent = existingEvents.next();
                            if (existingEvent.eventType == EventBean.EVENT_TYPE_VIEW && view.id.equals(existingEvent.targetId) && viewEvent.equals(existingEvent.eventName)) {
                                viewEventExists = true;
                                break;
                            }
                        }
                        if (viewEvent.equals("onBindCustomView") && (view.customView.equals("") || view.customView.equals("none"))) {
                            viewEventExists = true;
                        }
                        if (!viewEventExists) {
                            addableViewEvents.add(new EventBean(EventBean.EVENT_TYPE_VIEW, view.type, view.id, viewEvent));
                        }
                    }
                }
            }
        }
        if (components != null) {
            Iterator<ComponentBean> it3 = components.iterator();
            while (it3.hasNext()) {
                ComponentBean component = it3.next();
                String[] componentEvents = oq.a(component.getClassInfo());
                if (componentEvents != null) {
                    for (String componentEvent : componentEvents) {
                        Iterator<EventBean> it4 = jC.a(sc_id).g(projectFile.getJavaName()).iterator();
                        boolean componentEventExists;
                        while (true) {
                            if (!it4.hasNext()) {
                                componentEventExists = false;
                                break;
                            }
                            EventBean existingComponentEvent = it4.next();
                            if (existingComponentEvent.eventType == EventBean.EVENT_TYPE_COMPONENT && component.componentId.equals(existingComponentEvent.targetId) && componentEvent.equals(existingComponentEvent.eventName)) {
                                componentEventExists = true;
                                break;
                            }
                        }
                        if (!componentEventExists) {
                            addableComponentEvents.add(new EventBean(EventBean.EVENT_TYPE_COMPONENT, component.type, component.componentId, componentEvent));
                        }
                    }
                }
            }
        }
        ViewBean fab;
        if (projectFile.hasActivityOption(ProjectFileBean.OPTION_ACTIVITY_FAB) && (fab = jC.a(sc_id).h(projectFile.getXmlName())) != null) {
            for (String fabEvent : oq.c(fab.getClassInfo())) {
                Iterator<EventBean> it5 = jC.a(sc_id).g(projectFile.getJavaName()).iterator();
                boolean fabEventExists;
                while (true) {
                    if (!it5.hasNext()) {
                        fabEventExists = false;
                        break;
                    }
                    EventBean existingFabEvent = it5.next();
                    if (existingFabEvent.eventType == EventBean.EVENT_TYPE_VIEW && fab.id.equals(existingFabEvent.targetId) && fabEvent.equals(existingFabEvent.eventName)) {
                        fabEventExists = true;
                        break;
                    }
                }
                if (!fabEventExists) {
                    addableViewEvents.add(new EventBean(EventBean.EVENT_TYPE_VIEW, fab.type, fab.id, fabEvent));
                }
            }
        }
        if (projectFile.hasActivityOption(ProjectFileBean.OPTION_ACTIVITY_DRAWER)) {
            ArrayList<ViewBean> drawerViews = jC.a(sc_id).d(projectFile.getDrawerXmlName());
            if (drawerViews != null) {
                Iterator<ViewBean> it6 = drawerViews.iterator();
                while (it6.hasNext()) {
                    ViewBean drawerView = it6.next();
                    for (String drawerViewEvent : oq.c(drawerView.getClassInfo())) {
                        Iterator<EventBean> it7 = jC.a(sc_id).g(projectFile.getJavaName()).iterator();
                        boolean drawerViewEventExists;
                        while (true) {
                            if (!it7.hasNext()) {
                                drawerViewEventExists = false;
                                break;
                            }
                            EventBean existingDrawerViewEvent = it7.next();
                            if (existingDrawerViewEvent.eventType == EventBean.EVENT_TYPE_DRAWER_VIEW && drawerView.id.equals(existingDrawerViewEvent.targetId) && drawerViewEvent.equals(existingDrawerViewEvent.eventName)) {
                                drawerViewEventExists = true;
                                break;
                            }
                        }
                        if (!drawerViewEventExists) {
                            addableDrawerViewEvents.add(new EventBean(EventBean.EVENT_TYPE_DRAWER_VIEW, drawerView.type, drawerView.id, drawerViewEvent));
                        }
                    }
                }
            }
        }
        if (categoryAdapter.lastSelectedCategory == -1) {
            eventAdapter.setEvents(categories.get(Integer.valueOf(categoryIndex)));
            categoryAdapter.lastSelectedCategory = categoryIndex;
            tv_category.setText(rs.a(getApplicationContext(), categoryIndex));
            if (categoryAdapter != null) {
                categoryAdapter.notifyItemChanged(categoryIndex);
            }
            if (categoryIndex == 4) {
                moreblock_layout.setVisibility(View.VISIBLE);
                empty_message.setVisibility(View.GONE);
                event_list.setVisibility(View.GONE);
            } else {
                moreblock_layout.setVisibility(View.GONE);
                event_list.setVisibility(View.VISIBLE);
            }
        }
        if (eventAdapter != null) {
            eventAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onClick(View v) {
        boolean finished = false;
        if (!mB.a()) {
            int id = v.getId();
            if (id == R.id.add_button) {
                if (eventsToAdd.size() != 0 || !moreBlockView.a()) {
                    if (!moreBlockView.a()) {
                        if (!moreBlockView.b()) {
                            eventAdapter.setEvents(categories.get(4));
                            categoryAdapter.lastSelectedCategory = 4;
                            tv_category.setText(rs.a(getApplicationContext(), 4));
                            empty_message.setVisibility(View.GONE);
                            moreblock_layout.setVisibility(View.VISIBLE);
                            categoryAdapter.notifyDataSetChanged();
                            finished = true;
                        } else {
                            Pair<String, String> blockInformation = moreBlockView.getBlockInformation();
                            jC.a(sc_id).a(projectFile.getJavaName(), blockInformation.first, blockInformation.second);
                        }
                    }
                    if (!finished) {
                        Iterator<EventBean> it = eventsToAdd.iterator();
                        while (it.hasNext()) {
                            jC.a(sc_id).a(projectFile.getJavaName(), it.next());
                        }
                        if (eventsToAdd.size() == 1) {
                            bB.a(getApplicationContext(), xB.b().a(getApplicationContext(), R.string.event_message_new_event), bB.TOAST_NORMAL).show();
                        } else if (eventsToAdd.size() > 1) {
                            bB.a(getApplicationContext(), xB.b().a(getApplicationContext(), R.string.event_message_new_events), bB.TOAST_NORMAL).show();
                        }
                        jC.a(sc_id).k();
                        setResult(RESULT_OK);
                        finish();
                    }
                }
            } else if (id == R.id.cancel_button) {
                setResult(RESULT_CANCELED);
                finish();
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.logic_popup_add_event);
        Intent intent = getIntent();
        if (savedInstanceState == null) {
            sc_id = intent.getStringExtra("sc_id");
            projectFile = intent.getParcelableExtra("project_file");
            categoryIndex = intent.getIntExtra("category_index", 0);
        } else {
            sc_id = savedInstanceState.getString("sc_id");
            projectFile = savedInstanceState.getParcelable("project_file");
            categoryIndex = savedInstanceState.getInt("category_index");
        }
        event_list = findViewById(R.id.event_list);
        tv_category = findViewById(R.id.tv_category);
        RecyclerView category_list = findViewById(R.id.category_list);
        events_preview = findViewById(R.id.events_preview);
        container = findViewById(R.id.container);
        add_button = findViewById(R.id.add_button);
        cancel_button = findViewById(R.id.cancel_button);
        empty_message = findViewById(R.id.empty_message);
        moreblock_layout = findViewById(R.id.moreblock_layout);
        moreBlockView = new dt(this);
        moreblock_layout.addView(moreBlockView);
        moreblock_layout.setVisibility(View.GONE);
        add_button.setOnClickListener(this);
        cancel_button.setOnClickListener(this);
        categories = new HashMap<>();
        addableEtcEvents = new ArrayList<>();
        addableViewEvents = new ArrayList<>();
        addableComponentEvents = new ArrayList<>();
        addableActivityEvents = new ArrayList<>();
        addableDrawerViewEvents = new ArrayList<>();
        categories.put(0, addableActivityEvents);
        categories.put(1, addableViewEvents);
        categories.put(2, addableComponentEvents);
        categories.put(3, addableDrawerViewEvents);
        categories.put(4, addableEtcEvents);
        event_list.setHasFixedSize(true);
        event_list.setLayoutManager(new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false));
        eventAdapter = new EventAdapter();
        event_list.setAdapter(eventAdapter);
        category_list.setHasFixedSize(true);
        categoryAdapter = new CategoryAdapter();
        category_list.setLayoutManager(new LinearLayoutManager(getApplicationContext(), RecyclerView.HORIZONTAL, false));
        category_list.setAdapter(categoryAdapter);
        ((SimpleItemAnimator) category_list.getItemAnimator()).setSupportsChangeAnimations(false);
        events_preview.setHasFixedSize(true);
        eventsToAddAdapter = new EventsToAddAdapter();
        events_preview.setLayoutManager(new LinearLayoutManager(getApplicationContext(), RecyclerView.HORIZONTAL, false));
        events_preview.setAdapter(eventsToAddAdapter);
        C = true;
        events_preview.setVisibility(View.GONE);
        empty_message.setVisibility(View.GONE);
        eventsToAdd = new ArrayList<>();
        event_list.bringToFront();
        overridePendingTransition(R.anim.ani_fade_in, R.anim.ani_fade_out);
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        add_button.setText(xB.b().a(this, R.string.common_word_add));
        cancel_button.setText(xB.b().a(this, R.string.common_word_cancel));
        empty_message.setText(xB.b().a(this, R.string.event_message_no_avail_events));
        moreBlockView.setFuncNameValidator(jC.a(sc_id).a(projectFile));
    }

    @Override
    public void onResume() {
        super.onResume();
        gB.a(container, 500);
        if (projectFile != null) {
            initialize();
        }
        d.setScreenName(AddEventActivity.class.getSimpleName().toString());
        d.send(new HitBuilders.ScreenViewBuilder().build());
    }

    @Override
    public void onSaveInstanceState(Bundle newState) {
        newState.putString("sc_id", sc_id);
        newState.putParcelable("project_file", projectFile);
        newState.putInt("category_index", categoryIndex);
        super.onSaveInstanceState(newState);
    }

    private class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {
        private int lastSelectedEvent = -1;
        private ArrayList<EventBean> events = new ArrayList<>();
        private boolean e;

        private class ViewHolder extends RecyclerView.ViewHolder {
            public final LinearLayout events_preview;
            public final ImageView img_icon;
            public final TextView tv_target_type;
            public final TextView tv_sep;
            public final TextView tv_target_id;
            public final TextView tv_event_name;
            public final CheckBox checkbox;

            public ViewHolder(View itemView) {
                super(itemView);
                events_preview = itemView.findViewById(R.id.events_preview);
                img_icon = itemView.findViewById(R.id.img_icon);
                tv_target_type = itemView.findViewById(R.id.tv_target_type);
                tv_sep = itemView.findViewById(R.id.tv_sep);
                tv_target_id = itemView.findViewById(R.id.tv_target_id);
                tv_event_name = itemView.findViewById(R.id.tv_event_name);
                checkbox = itemView.findViewById(R.id.checkbox);
                itemView.setOnClickListener(v -> {
                    if (!mB.a()) {
                        lastSelectedEvent = getLayoutPosition();
                        EventBean event = categories.get(categoryAdapter.lastSelectedCategory).get(lastSelectedEvent);
                        if (event.isSelected) {
                            event.isSelected = false;
                            eventsToAdd.remove(event);
                            l();
                            eventsToAddAdapter.notifyItemRemoved(eventsToAddAdapter.getItemCount());
                        } else {
                            event.isSelected = true;
                            eventsToAdd.add(event);
                            l();
                            eventsToAddAdapter.notifyItemInserted(eventsToAddAdapter.getItemCount());
                        }
                        if (!e) {
                            notifyItemChanged(lastSelectedEvent);
                        }
                    }
                });
                checkbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    lastSelectedEvent = getLayoutPosition();
                    EventBean event = categories.get(categoryAdapter.lastSelectedCategory).get(lastSelectedEvent);
                    if (!event.isSelected && isChecked) {
                        event.isSelected = true;
                        eventsToAdd.add(event);
                        l();
                        eventsToAddAdapter.notifyItemInserted(eventsToAddAdapter.getItemCount());
                    } else if (event.isSelected && !isChecked) {
                        event.isSelected = false;
                        eventsToAdd.remove(event);
                        eventsToAddAdapter.notifyItemRemoved(eventsToAddAdapter.getItemCount());
                        l();
                    }
                    if (!e) {
                        notifyItemChanged(lastSelectedEvent);
                    }
                });
            }
        }

        public EventAdapter() {
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            e = true;
            holder.events_preview.removeAllViews();
            holder.events_preview.setVisibility(View.VISIBLE);
            EventBean event = categories.get(Integer.valueOf(categoryAdapter.lastSelectedCategory)).get(position);
            ImageView imageView = new ImageView(getApplicationContext());
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(0, 0, (int) wB.a(getApplicationContext(), 2.0f), 0);
            int a = (int) wB.a(getApplicationContext(), 16.0f);
            layoutParams.width = a;
            layoutParams.height = a;
            imageView.setLayoutParams(layoutParams);
            imageView.setImageResource(oq.a(event.eventName));
            holder.events_preview.addView(imageView);
            holder.img_icon.setImageResource(EventBean.getEventIconResource(event.eventType, event.targetType));
            int eventType = event.eventType;
            if (eventType == EventBean.EVENT_TYPE_ACTIVITY) {
                holder.tv_target_type.setText("Activity");
                holder.events_preview.setVisibility(View.GONE);
            } else if (eventType == EventBean.EVENT_TYPE_VIEW) {
                holder.tv_target_type.setText(ViewBean.getViewTypeName(event.targetType));
            } else if (eventType == EventBean.EVENT_TYPE_DRAWER_VIEW) {
                holder.tv_target_type.setText(ViewBean.getViewTypeName(event.targetType));
            } else if (eventType == EventBean.EVENT_TYPE_COMPONENT) {
                holder.tv_target_type.setText(ComponentBean.getComponentName(getApplicationContext(), event.targetType));
            } else if (eventType == EventBean.EVENT_TYPE_ETC) {
                holder.events_preview.setVisibility(View.GONE);
            }
            holder.tv_sep.setText(" : ");
            if (event.targetId.equals("_fab")) {
                holder.tv_target_id.setText("fab");
            } else {
                holder.tv_target_id.setText(event.targetId);
            }
            holder.tv_event_name.setText(oq.a(event.eventName, getApplicationContext()));
            if (event.isSelected) {
                holder.checkbox.setChecked(true);
            } else {
                holder.checkbox.setChecked(false);
            }
            e = false;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.fr_logic_list_item_addevent, parent, false));
        }

        @Override
        public int getItemCount() {
            return events.size();
        }

        private void setEvents(ArrayList<EventBean> events) {
            if (events.size() == 0) {
                empty_message.setVisibility(View.VISIBLE);
            } else {
                empty_message.setVisibility(View.GONE);
                event_list.setVisibility(View.VISIBLE);
            }
            this.events = events;
        }
    }

    private class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
        private int lastSelectedCategory = -1;

        private class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            public final LinearLayout container;
            public final ImageView img_icon;

            public ViewHolder(View itemView) {
                super(itemView);
                img_icon = itemView.findViewById(R.id.img_icon);
                container = itemView.findViewById(R.id.container);
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                if (getLayoutPosition() != -1) {
                    int layoutPosition = getLayoutPosition();
                    if (layoutPosition != lastSelectedCategory) {
                        lastSelectedCategory = getLayoutPosition();
                        notifyDataSetChanged();
                        tv_category.setText(rs.a(getApplicationContext(), lastSelectedCategory));
                        if (lastSelectedCategory == 4) {
                            moreblock_layout.setVisibility(View.VISIBLE);
                            empty_message.setVisibility(View.GONE);
                        } else {
                            moreblock_layout.setVisibility(View.GONE);
                            eventAdapter.setEvents(categories.get(Integer.valueOf(lastSelectedCategory)));
                            eventAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        }

        public CategoryAdapter() {
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.img_icon.setImageResource(rs.a(position));
            if (lastSelectedCategory == position) {
                holder.container.setBackgroundResource(R.drawable.border_top_corner_white_no_stroke);
                holder.img_icon.animate().scaleX(1.0f).scaleY(1.0f).alpha(1.0f).start();
                holder.container.animate().translationY(0.0f).start();
            } else {
                holder.container.setBackgroundResource(R.drawable.border_top_corner_grey_no_stroke);
                holder.img_icon.animate().scaleX(0.8f).scaleY(0.8f).alpha(0.6f).start();
                holder.container.setTranslationY(wB.a(getApplicationContext(), 12.0f));
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.fr_logic_list_category_icon_item, parent, false);
            inflate.setLayoutParams(new RecyclerView.LayoutParams(parent.getMeasuredWidth() / getItemCount(), (int) wB.a(getApplicationContext(), 44.0f)));
            inflate.setTranslationY(wB.a(getApplicationContext(), 12.0f));
            inflate.findViewById(R.id.img_icon).setAlpha(0.6f);
            inflate.findViewById(R.id.img_icon).setScaleX(0.8f);
            inflate.findViewById(R.id.img_icon).setScaleY(0.8f);
            return new ViewHolder(inflate);
        }

        @Override
        public int getItemCount() {
            return categories.size();
        }
    }

    private class EventsToAddAdapter extends RecyclerView.Adapter<EventsToAddAdapter.ViewHolder> {
        private class ViewHolder extends RecyclerView.ViewHolder {
            public LinearLayout ll_img_event;
            public RelativeLayout container;
            public ImageView img_icon;
            public ImageView img_event;

            public ViewHolder(View itemView) {
                super(itemView);
                container = itemView.findViewById(R.id.container);
                img_icon = itemView.findViewById(R.id.img_icon);
                img_event = itemView.findViewById(R.id.img_event);
                ll_img_event = itemView.findViewById(R.id.ll_img_event);
            }
        }

        public EventsToAddAdapter() {
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.ll_img_event.setVisibility(View.VISIBLE);
            EventBean event = eventsToAdd.get(position);
            int eventType = event.eventType;
            if (eventType == EventBean.EVENT_TYPE_ACTIVITY) {
                holder.ll_img_event.setVisibility(View.GONE);
            } else if (eventType == EventBean.EVENT_TYPE_ETC) {
                holder.ll_img_event.setVisibility(View.GONE);
            }
            holder.img_icon.setImageResource(EventBean.getEventIconResource(event.eventType, event.targetType));
            holder.img_event.setImageResource(oq.a(event.eventName));
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.fr_logic_list_preview_with_event_item, parent, false));
        }

        @Override
        public int getItemCount() {
            return eventsToAdd.size();
        }
    }
}
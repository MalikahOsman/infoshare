package infoshare.client.content.systemValues.views;

import com.vaadin.data.Property;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Button;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;
import infoshare.client.content.systemValues.forms.ContentTypeForm;
import infoshare.client.content.systemValues.models.ContentTypeModel;
import infoshare.client.content.systemValues.SystemValues;
import infoshare.client.content.systemValues.tables.ContentTypeTable;
import infoshare.client.content.MainLayout;
import infoshare.domain.ContentType;
import infoshare.services.ContentType.ContentTypeService;
import infoshare.services.ContentType.Impl.ContentTypeServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by codex on 2015/06/25.
 */
public class ContentTypeView extends VerticalLayout implements Button.ClickListener, Property.ValueChangeListener {

    @Autowired
    private ContentTypeService contentTypeService = new ContentTypeServiceImpl();

    private final MainLayout main;
    private final ContentTypeForm form;
    private final ContentTypeTable table;

    public ContentTypeView(MainLayout mainApp) {
        this.main = mainApp;
        this.form = new ContentTypeForm();
        this.table = new ContentTypeTable(main);
        setSizeFull();
        addComponent(form);
        addComponent(table);
        addListeners();
    }
    @Override
    public void buttonClick(Button.ClickEvent event) {
        final Button source = event.getButton();
        if (source == form.save) {
            saveForm(form.binder);
        } else if (source == form.edit) {
            setEditFormProperties();
        } else if (source == form.cancel) {
            getHome();
        } else if (source == form.update) {
            saveEditedForm(form.binder);
        } else if (source == form.delete) {
            deleteForm(form.binder);
        }
    }

    @Override
    public void valueChange(Property.ValueChangeEvent event) {
        final Property property = event.getProperty();
        if (property == table) {
            final ContentType contentType = contentTypeService.find(table.getValue().toString());
            final ContentTypeModel bean = getModel(contentType);
            form.binder.setItemDataSource(new BeanItem<>(bean));
            setReadFormProperties();
        }
    }

    private void saveForm(FieldGroup binder) {
        try {
            binder.commit();
            contentTypeService.save(getUpdateEntity(binder));
            getHome();
            Notification.show("Record ADDED!", Notification.Type.HUMANIZED_MESSAGE);
        } catch (FieldGroup.CommitException e) {
            Notification.show("Values MISSING!", Notification.Type.HUMANIZED_MESSAGE);
            getHome();
        }
    }

    private void saveEditedForm(FieldGroup binder) {
        try {
            binder.commit();
            contentTypeService.merge(getUpdateEntity(binder));
            getHome();
            Notification.show("Record UPDATED!", Notification.Type.HUMANIZED_MESSAGE);
        } catch (FieldGroup.CommitException e) {
            Notification.show("Values MISSING!", Notification.Type.HUMANIZED_MESSAGE);
            getHome();
        }
    }

    private void deleteForm(FieldGroup binder) {
        final ContentType contentType = getUpdateEntity(binder);
        contentTypeService.remove(contentType);
        getHome();
    }
    private ContentType getUpdateEntity(FieldGroup binder) {
        final ContentTypeModel bean = ((BeanItem<ContentTypeModel>) binder.getItemDataSource()).getBean();
        final ContentType contentType = new ContentType.Builder(bean.getContentTyeName())
                .contentTyeDescription(bean.getContentTyeDescription())
                .id(bean.getId())
                .build();
        return contentType;
    }

    private void getHome() {
      main.content.setSecondComponent(new SystemValues(main, "LANDING"));
    }

    private void setEditFormProperties() {
        form.binder.setReadOnly(false);
        form.save.setVisible(false);
        form.edit.setVisible(false);
        form.cancel.setVisible(true);
        form.delete.setVisible(false);
        form.update.setVisible(true);
    }

    private void setReadFormProperties() {
        form.binder.setReadOnly(true);
        //Buttons Behaviou
        form.save.setVisible(false);
        form.edit.setVisible(true);
        form.cancel.setVisible(true);
        form.delete.setVisible(true);
        form.update.setVisible(false);
    }

    private void addListeners() {
        //Register Button Listeners
        form.save.addClickListener((Button.ClickListener) this);
        form.edit.addClickListener((Button.ClickListener) this);
        form.cancel.addClickListener((Button.ClickListener) this);
        form.update.addClickListener((Button.ClickListener) this);
        form.delete.addClickListener((Button.ClickListener) this);
        //Register Table Listerners
        table.addValueChangeListener((Property.ValueChangeListener) this);
    }

    private ContentTypeModel getModel(ContentType val) {
        final ContentTypeModel model = new ContentTypeModel();
        final ContentType contentType = contentTypeService.find(val.getId());
        model.setContentTyeName(contentType.getContentTyeName());
        model.setId(contentType.getId());
        model.setContentTyeDescription(contentType.getContentTyeDescription());
        return model;
    }

}
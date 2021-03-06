package infoshare.client.content.common.util.tables;

import com.vaadin.ui.Table;
import infoshare.app.facade.UtilFacade;
import infoshare.client.content.MainLayout;
import infoshare.domain.storage.StorageUrl;

import java.util.Set;

/**
 * Created by hashcode on 2016/01/06.
 */
public class StorageUrlTable extends Table {
    private final MainLayout main;

    public StorageUrlTable(MainLayout main) {
        this.main = main;
        setSizeFull();

        addContainerProperty("Link Name", String.class, null);
        addContainerProperty("Link URL", String.class, null);

        // Add Data Columns
        Set<StorageUrl> storageUrls = UtilFacade.getStorageUrlServiceInstance().getAllLinks();
        for (StorageUrl storageUrl : storageUrls) {
            addItem(new Object[]{storageUrl.getName(), storageUrl.getUrl()}, storageUrl.getId());
        }
        // Allow selecting items from the table.
        setNullSelectionAllowed(false);

        setSelectable(true);
        // Send changes in selection immediately to server.
        setImmediate(true);

    }
}

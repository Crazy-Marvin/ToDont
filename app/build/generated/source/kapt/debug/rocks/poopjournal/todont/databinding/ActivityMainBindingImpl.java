package rocks.poopjournal.todont.databinding;
import rocks.poopjournal.todont.R;
import rocks.poopjournal.todont.BR;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.View;
@SuppressWarnings("unchecked")
public class ActivityMainBindingImpl extends ActivityMainBinding  {

    @Nullable
    private static final androidx.databinding.ViewDataBinding.IncludedLayouts sIncludes;
    @Nullable
    private static final android.util.SparseIntArray sViewsWithIds;
    static {
        sIncludes = null;
        sViewsWithIds = new android.util.SparseIntArray();
        sViewsWithIds.put(R.id.appbar, 1);
        sViewsWithIds.put(R.id.toolbar, 2);
        sViewsWithIds.put(R.id.toolbartext, 3);
        sViewsWithIds.put(R.id.label, 4);
        sViewsWithIds.put(R.id.settings, 5);
        sViewsWithIds.put(R.id.container, 6);
        sViewsWithIds.put(R.id.divider, 7);
        sViewsWithIds.put(R.id.navigationView, 8);
        sViewsWithIds.put(R.id.floatingbtn, 9);
    }
    // views
    // variables
    // values
    // listeners
    // Inverse Binding Event Handlers

    public ActivityMainBindingImpl(@Nullable androidx.databinding.DataBindingComponent bindingComponent, @NonNull View root) {
        this(bindingComponent, root, mapBindings(bindingComponent, root, 10, sIncludes, sViewsWithIds));
    }
    private ActivityMainBindingImpl(androidx.databinding.DataBindingComponent bindingComponent, View root, Object[] bindings) {
        super(bindingComponent, root, 0
            , (com.google.android.material.appbar.AppBarLayout) bindings[1]
            , (android.widget.FrameLayout) bindings[6]
            , (android.view.View) bindings[7]
            , (com.google.android.material.floatingactionbutton.FloatingActionButton) bindings[9]
            , (android.widget.ImageButton) bindings[4]
            , (androidx.constraintlayout.widget.ConstraintLayout) bindings[0]
            , (com.google.android.material.bottomnavigation.BottomNavigationView) bindings[8]
            , (android.widget.ImageButton) bindings[5]
            , (androidx.appcompat.widget.Toolbar) bindings[2]
            , (android.widget.TextView) bindings[3]
            );
        this.main.setTag(null);
        setRootTag(root);
        // listeners
        invalidateAll();
    }

    @Override
    public void invalidateAll() {
        synchronized(this) {
                mDirtyFlags = 0x1L;
        }
        requestRebind();
    }

    @Override
    public boolean hasPendingBindings() {
        synchronized(this) {
            if (mDirtyFlags != 0) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean setVariable(int variableId, @Nullable Object variable)  {
        boolean variableSet = true;
            return variableSet;
    }

    @Override
    protected boolean onFieldChange(int localFieldId, Object object, int fieldId) {
        switch (localFieldId) {
        }
        return false;
    }

    @Override
    protected void executeBindings() {
        long dirtyFlags = 0;
        synchronized(this) {
            dirtyFlags = mDirtyFlags;
            mDirtyFlags = 0;
        }
        // batch finished
    }
    // Listener Stub Implementations
    // callback impls
    // dirty flag
    private  long mDirtyFlags = 0xffffffffffffffffL;
    /* flag mapping
        flag 0 (0x1L): null
    flag mapping end*/
    //end
}
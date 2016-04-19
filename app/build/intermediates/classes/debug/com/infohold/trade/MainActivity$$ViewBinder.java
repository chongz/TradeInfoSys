// Generated code from Butter Knife. Do not modify!
package com.infohold.trade;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class MainActivity$$ViewBinder<T extends com.infohold.trade.MainActivity> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131624191, "field 'toolbar'");
    target.toolbar = finder.castView(view, 2131624191, "field 'toolbar'");
    view = finder.findRequiredView(source, 2131624107, "field 'drawerLayout'");
    target.drawerLayout = finder.castView(view, 2131624107, "field 'drawerLayout'");
    view = finder.findRequiredView(source, 2131624108, "field 'navigationView'");
    target.navigationView = finder.castView(view, 2131624108, "field 'navigationView'");
    view = finder.findRequiredView(source, 2131624118, "field 'ptrFrameLayout'");
    target.ptrFrameLayout = finder.castView(view, 2131624118, "field 'ptrFrameLayout'");
    view = finder.findRequiredView(source, 2131624119, "field 'loadMoreListViewContainer'");
    target.loadMoreListViewContainer = finder.castView(view, 2131624119, "field 'loadMoreListViewContainer'");
    view = finder.findRequiredView(source, 2131624120, "field 'bulletinListView'");
    target.bulletinListView = finder.castView(view, 2131624120, "field 'bulletinListView'");
  }

  @Override public void unbind(T target) {
    target.toolbar = null;
    target.drawerLayout = null;
    target.navigationView = null;
    target.ptrFrameLayout = null;
    target.loadMoreListViewContainer = null;
    target.bulletinListView = null;
  }
}

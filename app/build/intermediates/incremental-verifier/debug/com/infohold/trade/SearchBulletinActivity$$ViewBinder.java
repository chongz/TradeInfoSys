// Generated code from Butter Knife. Do not modify!
package com.infohold.trade;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class SearchBulletinActivity$$ViewBinder<T extends com.infohold.trade.SearchBulletinActivity> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131624118, "field 'ptrFrameLayout'");
    target.ptrFrameLayout = finder.castView(view, 2131624118, "field 'ptrFrameLayout'");
    view = finder.findRequiredView(source, 2131624119, "field 'loadMoreListViewContainer'");
    target.loadMoreListViewContainer = finder.castView(view, 2131624119, "field 'loadMoreListViewContainer'");
    view = finder.findRequiredView(source, 2131624120, "field 'bulletinListView'");
    target.bulletinListView = finder.castView(view, 2131624120, "field 'bulletinListView'");
    view = finder.findRequiredView(source, 2131624189, "field 'toolbar'");
    target.toolbar = finder.castView(view, 2131624189, "field 'toolbar'");
    view = finder.findRequiredView(source, 2131624190, "field 'searchView'");
    target.searchView = finder.castView(view, 2131624190, "field 'searchView'");
  }

  @Override public void unbind(T target) {
    target.ptrFrameLayout = null;
    target.loadMoreListViewContainer = null;
    target.bulletinListView = null;
    target.toolbar = null;
    target.searchView = null;
  }
}

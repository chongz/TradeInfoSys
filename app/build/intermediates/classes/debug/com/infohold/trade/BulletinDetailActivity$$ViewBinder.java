// Generated code from Butter Knife. Do not modify!
package com.infohold.trade;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class BulletinDetailActivity$$ViewBinder<T extends com.infohold.trade.BulletinDetailActivity> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131624090, "field 'webView'");
    target.webView = finder.castView(view, 2131624090, "field 'webView'");
    view = finder.findRequiredView(source, 2131624191, "field 'toolbar'");
    target.toolbar = finder.castView(view, 2131624191, "field 'toolbar'");
  }

  @Override public void unbind(T target) {
    target.webView = null;
    target.toolbar = null;
  }
}

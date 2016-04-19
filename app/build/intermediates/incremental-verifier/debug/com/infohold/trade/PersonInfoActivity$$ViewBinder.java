// Generated code from Butter Knife. Do not modify!
package com.infohold.trade;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class PersonInfoActivity$$ViewBinder<T extends com.infohold.trade.PersonInfoActivity> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131624191, "field 'toolbar'");
    target.toolbar = finder.castView(view, 2131624191, "field 'toolbar'");
    view = finder.findRequiredView(source, 2131624110, "field 'listView'");
    target.listView = finder.castView(view, 2131624110, "field 'listView'");
    view = finder.findRequiredView(source, 2131624112, "field 'okBtn' and method 'addOrder'");
    target.okBtn = finder.castView(view, 2131624112, "field 'okBtn'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.addOrder();
        }
      });
    view = finder.findRequiredView(source, 2131624113, "field 'cancelBtn' and method 'cancelOrder'");
    target.cancelBtn = finder.castView(view, 2131624113, "field 'cancelBtn'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.cancelOrder();
        }
      });
  }

  @Override public void unbind(T target) {
    target.toolbar = null;
    target.listView = null;
    target.okBtn = null;
    target.cancelBtn = null;
  }
}

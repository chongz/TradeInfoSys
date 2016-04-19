// Generated code from Butter Knife. Do not modify!
package com.infohold.trade;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class PersonInfoAdapter$ViewHolder$$ViewBinder<T extends com.infohold.trade.PersonInfoAdapter.ViewHolder> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131624114, "field 'nameView'");
    target.nameView = finder.castView(view, 2131624114, "field 'nameView'");
    view = finder.findRequiredView(source, 2131624115, "field 'valueView'");
    target.valueView = finder.castView(view, 2131624115, "field 'valueView'");
  }

  @Override public void unbind(T target) {
    target.nameView = null;
    target.valueView = null;
  }
}

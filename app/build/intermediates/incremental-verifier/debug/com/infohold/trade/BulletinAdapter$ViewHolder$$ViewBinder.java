// Generated code from Butter Knife. Do not modify!
package com.infohold.trade;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class BulletinAdapter$ViewHolder$$ViewBinder<T extends com.infohold.trade.BulletinAdapter.ViewHolder> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131624091, "field 'imageView'");
    target.imageView = finder.castView(view, 2131624091, "field 'imageView'");
    view = finder.findRequiredView(source, 2131624092, "field 'textView'");
    target.textView = finder.castView(view, 2131624092, "field 'textView'");
    view = finder.findRequiredView(source, 2131624093, "field 'btn'");
    target.btn = finder.castView(view, 2131624093, "field 'btn'");
  }

  @Override public void unbind(T target) {
    target.imageView = null;
    target.textView = null;
    target.btn = null;
  }
}

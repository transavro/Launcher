/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package presenter;

import android.view.ViewGroup;

import androidx.leanback.widget.Presenter;
import widget.CharacterCardView;

/**
 * This Presenter is used to display the characters card row in the DetailView examples.
 */
public class CharacterCardPresenter extends Presenter
{

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent)
    {
        CharacterCardView characterCardView = null;
        try {
            characterCardView = new CharacterCardView(parent.getContext());
            characterCardView.setFocusable(true);
            characterCardView.setFocusableInTouchMode(true);
            return new ViewHolder(characterCardView);
        } catch (Exception e) {
            e.printStackTrace();
            return new ViewHolder(characterCardView);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Object item)
    {
        CharacterCardView characterCardView = (CharacterCardView) viewHolder.view;
        try {
            characterCardView.updateUi(item);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUnbindViewHolder(ViewHolder viewHolder) {

    }
}

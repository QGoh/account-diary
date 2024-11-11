/*
 * Copyright (c) 2018, Magic fTail
 * Copyright (c) 2020, Jordan <nightfirecat@protonmail.com>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.accountdiary;

import java.awt.Color;
import java.awt.event.KeyEvent;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.Experience;
import net.runelite.api.FontID;
import net.runelite.api.Skill;
import net.runelite.api.widgets.ComponentID;
import net.runelite.api.widgets.JavaScriptCallback;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetPositionMode;
import net.runelite.api.widgets.WidgetSizeMode;
import net.runelite.api.widgets.WidgetTextAlignment;
import net.runelite.api.widgets.WidgetType;
import net.runelite.client.game.chatbox.ChatboxInput;
import net.runelite.client.input.KeyListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

@Slf4j
class FakeChatBoxMessage extends ChatboxInput implements KeyListener
{
    private static final int X_OFFSET = 13;
    private static final int Y_OFFSET = 16;

    @Inject
    private AccountDiaryPlugin plugin;

    @Inject
    private Client client;

    @Getter
    private boolean closeMessage;
    private String message;

    FakeChatBoxMessage(String message) {
        this.message = message;
    }

    @Override
    public void open()
    {
        // TODO: add sound event for level-up (need to find sound IDs)
        //plugin.getClientThread().invoke(this::setFireworksGraphic);

        final Widget chatboxContainer = plugin.getChatboxPanelManager().getContainerWidget();

        final Widget body = chatboxContainer.createChild(-1, WidgetType.TEXT);
        final Widget footer = chatboxContainer.createChild(-1, WidgetType.TEXT);

        body.setText(message);
        body.setFontId(FontID.QUILL_8);
        body.setXPositionMode(WidgetPositionMode.ABSOLUTE_TOP);
        body.setOriginalX(X_OFFSET);
        body.setOriginalY(Y_OFFSET);
        body.setYPositionMode(WidgetPositionMode.ABSOLUTE_TOP);
        body.setOriginalWidth(390);
        body.setOriginalHeight(30);
        body.setXTextAlignment(WidgetTextAlignment.CENTER);
        body.setYTextAlignment(WidgetTextAlignment.CENTER);
        body.setWidthMode(WidgetSizeMode.ABSOLUTE);
        body.revalidate();

        footer.setText("Click here to continue");
        footer.setTextColor(Color.BLUE.getRGB());
        footer.setFontId(FontID.QUILL_8);
        footer.setXPositionMode(WidgetPositionMode.ABSOLUTE_TOP);
        footer.setOriginalX(73 + X_OFFSET);
        footer.setYPositionMode(WidgetPositionMode.ABSOLUTE_TOP);
        footer.setOriginalY(74 + Y_OFFSET);
        footer.setOriginalWidth(390);
        footer.setOriginalHeight(17);
        footer.setXTextAlignment(WidgetTextAlignment.CENTER);
        footer.setYTextAlignment(WidgetTextAlignment.LEFT);
        footer.setWidthMode(WidgetSizeMode.ABSOLUTE);
        footer.setAction(0, "Continue");
        footer.setOnOpListener((JavaScriptCallback) ev -> triggerCloseViaMessage());
        footer.setOnMouseOverListener((JavaScriptCallback) ev -> footer.setTextColor(Color.WHITE.getRGB()));
        footer.setOnMouseLeaveListener((JavaScriptCallback) ev -> footer.setTextColor(Color.BLUE.getRGB()));
        footer.setHasListener(true);
        footer.revalidate();
    }

    @Override
    public void keyTyped(KeyEvent e)
    {
        if (e.getKeyChar() != ' ')
        {
            return;
        }

        triggerCloseViaMessage();

        e.consume();
    }

    @Override
    public void keyPressed(KeyEvent e)
    {
    }

    @Override
    public void keyReleased(KeyEvent e)
    {
    }

    void closeIfTriggered()
    {
        if (closeMessage && plugin.getChatboxPanelManager().getCurrentInput() == this)
        {
            plugin.getChatboxPanelManager().close();
        }
    }

    void triggerClose()
    {
        closeMessage = true;
    }

    private void triggerCloseViaMessage()
    {
        final Widget fakeFooter = client.getWidget(ComponentID.CHATBOX_CONTAINER).getChild(1);
        fakeFooter.setText("Please wait...");

        closeMessage = true;
    }
}
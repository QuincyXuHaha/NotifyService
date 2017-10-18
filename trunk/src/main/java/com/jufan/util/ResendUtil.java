package com.jufan.util;

import com.jufan.model.MessageModel;

import java.util.Date;

/**
 * @author 李尧
 * @since  0.2.0
 */
public class ResendUtil {

    public static ResendCheckState checkState(MessageModel message) {

        if ("finished".equals(message.getNowLevel()))
            return ResendCheckState.FINISHED;

        if ("0".equals(message.getNowLevel()) && message.getLastSendTime() == null) {

            if (new Date().getTime() - message.getFirstSendTime().getTime() >= 0)
                return ResendCheckState.FIRSTSEND;
            else
                return ResendCheckState.WAIT;
        }

        if (new Date().getTime() - message.getLastSendTime().getTime() >= 1000 * Integer.valueOf(message.getNowLevel()))
            return ResendCheckState.SEND;

        return ResendCheckState.WAIT;
    }

    public static void levelUp(MessageModel message) {

        if ("finished".equals(message.getNowLevel()))
            return;

        String[] levels = message.getResendStrategy().split(",");

        for (int i = 0; i < levels.length; i++) {
            if (message.getNowLevel().equals(levels[i])) {
                message.setNowLevel(i == levels.length - 1 ? "finished" : levels[i + 1]);
                return;
            }
        }
    }

}

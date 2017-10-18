package com.jufan.main.runner;

import com.jufan.dao.repository.MessageRepo;
import com.jufan.main.factory.depot.ResendDepot;
import com.jufan.main.factory.switches.Switches;
import com.jufan.entity.MessageEntity;
import com.jufan.model.MessageModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * 用于从数据库拉取数据并存入{@link ResendDepot}的worker
 *
 * @author 李尧
 * @since  0.1.0
 */
@Component
public class RunnablePuller implements Runnable {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private MessageRepo messageRepo;

    @Autowired
    private ResendDepot resendDepot;

    private LinkedList<MessageModel> messagesIn = new LinkedList<>();

    private int crashedCount = 0;

    @Override
    public void run() {
        try {
            while (true) {

                if (messagesIn.size() < 1 && Switches.PULLER_SWITCH)
                    messagesIn = getData();
                if (messagesIn.size() > 0)
                    resendDepot.produce(messagesIn);
            }
        } catch (Exception e) {
            if (crashedCount >= 5) {
                Switches.shutdown(e);
                return;
            }
            logger.error("RunnablePuller crashed! restart.", e);
            crashedCount++;
            run();
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    LinkedList<MessageModel> getData() throws Exception {
        List<MessageEntity> messages = messageRepo.findTop50000ByStatusAndFirstSendTimeLessThanEqual(-1, new Date());
        if (messages.size() > 0) {
            List<String> idList = messages.stream()
                    .map(MessageEntity::getId)
                    .collect(Collectors.toList());

            messageRepo.updateStatus(1, idList);

            for (MessageEntity message : messages)
                message.setStatus(1);
        }

        LinkedList<MessageModel> res = new LinkedList<>();
        for (MessageEntity message : messages)
            res.add(message.toModel());
        return res;
    }

    public int getStock() {
        return messagesIn.size();
    }
}

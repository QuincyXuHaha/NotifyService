package com.jufan.main.factory.consumer;

import com.jufan.main.factory.depot.UpdateDepot;
import com.jufan.main.factory.switches.Switches;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 *
 * {@link UpdateDepot}的消费者
 *
 * 自应用启动就会一直开启，单线程循环执行{@link UpdateDepot}的consume()方法
 *
 * @see UpdateDepot
 *
 * @author 李尧
 * @since  0.1.0
 */
@Component
public class UpdateConsumer implements Runnable {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private UpdateDepot updateDepot;

    private int crashedCount = 0;

    @Override
    public void run() {

        try {
            while (true) {

                if (!Switches.UPDATE_CONSUMER_SWITCH)
                    continue;

                updateDepot.consume();
            }
        } catch (Exception e) {
            if (crashedCount >= 5) {
                Switches.shutdown(e);
                return;
            }
            logger.error("UpdateConsumer crashed! restart.", e);
            crashedCount++;
            run();
        }
    }
}

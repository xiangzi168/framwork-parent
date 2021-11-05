package com.amg.framework.boot.canal.hander;

import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.Message;
import com.google.common.collect.Lists;
import com.amg.framework.boot.canal.entity.Canal;
import com.amg.framework.boot.canal.entity.Column;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.List;

/**
 * @author lyc
 * @date 2020/9/27 10:44
 * @describe
 */
public class SimpleMessageHandler implements MessageHandler<Message> {

    private static Logger log = LoggerFactory.getLogger(SimpleMessageHandler.class);

    private Option option;

    public Option getOption() {
        return option;
    }

    public void setOption(Option option) {
        this.option = option;
    }

    @Override
    public void handleMessage(Message message) {
        fillEntry(message);
    }

    private  void fillEntry(Message message) {
        long id = message.getId();
        List<CanalEntry.Entry> entrys = message.getEntries();
        for (CanalEntry.Entry entry : entrys) {

            if (entry.getEntryType() == CanalEntry.EntryType.TRANSACTIONBEGIN || entry.getEntryType() == CanalEntry.EntryType.TRANSACTIONEND) {
                continue;
            }
            CanalEntry.RowChange rowChage = null;
            try {
                rowChage = CanalEntry.RowChange.parseFrom(entry.getStoreValue());
            } catch (Exception e) {
                throw new RuntimeException("ERROR ## parser of eromanga-event has an error , data:" + entry.toString(),e);
            }
            CanalEntry.EventType eventType = rowChage.getEventType();

            Canal canal = new Canal();
            canal.setId(id);
            canal.setDatabase(entry.getHeader().getSchemaName());
            canal.setTable(entry.getHeader().getTableName());
            canal.setEventType(eventType.name());

            log.info(String.format("binlog[%s:%s] , name[%s,%s] , eventType : %s",
                    entry.getHeader().getLogfileName(), entry.getHeader().getLogfileOffset(),
                    entry.getHeader().getSchemaName(), entry.getHeader().getTableName(),
                    eventType));
            // 填充数据
            for (CanalEntry.RowData rowData : rowChage.getRowDatasList()) {
                if (eventType == CanalEntry.EventType.DELETE) {
                    canal.setBeforeList(Lists.newArrayList());
                    fillBeforeColumn(rowData.getBeforeColumnsList(),canal);
                    option.deleteBefore(canal);
                } else if (eventType == CanalEntry.EventType.INSERT) {
                    canal.setAfterList(Lists.newArrayList());
                    fillAfterColumn(rowData.getAfterColumnsList(),canal);
                    option.insertAfter(canal);
                } else if(eventType == CanalEntry.EventType.UPDATE) {
                    if(log.isDebugEnabled()){
                        log.debug("before");
                    }
                    canal.setBeforeList(Lists.newArrayList());
                    fillBeforeColumn(rowData.getBeforeColumnsList(),canal);
                    option.updateBefore(canal);
                    if(log.isDebugEnabled()){
                        log.debug("after");
                    }
                    canal.setAfterList(Lists.newArrayList());
                    fillAfterColumn(rowData.getAfterColumnsList(),canal);
                    option.updateAfter(canal);
                }else {
                    // 不做处理
                    printColumn(rowData.getAfterColumnsList());
                }
            }

        }
    }

    private static void fillBeforeColumn(List<CanalEntry.Column> columns, Canal canal) {
        List<Column> columnList = canal.getBeforeList();
        for (CanalEntry.Column column : columns) {
            Column newColumn = new Column();
            newColumn.setName(column.getName());
            newColumn.setValue(column.getValue());
            newColumn.setUpdate(column.getUpdated());
            columnList.add(newColumn);
        }
    }

    private static void fillAfterColumn(List<CanalEntry.Column> columns, Canal canal) {
        List<Column> columnList = canal.getAfterList();
        for (CanalEntry.Column column : columns) {
            Column newColumn = new Column();
            newColumn.setName(column.getName());
            newColumn.setValue(column.getValue());
            newColumn.setUpdate(column.getUpdated());
            columnList.add(newColumn);
        }
    }

    private static void printColumn(List<CanalEntry.Column> columns) {
        for (CanalEntry.Column column : columns) {
           log.info(column.getName() + " : " + column.getValue() + "    update=" + column.getUpdated());
        }
    }
}

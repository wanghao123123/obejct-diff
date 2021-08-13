package com.example.objectdiff.utils;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.example.objectdiff.diff.DiffNode;
import com.example.objectdiff.diff.Differ;
import com.example.objectdiff.diff.DifferFactory;
import com.example.objectdiff.log.SysLogObject;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

/**
 * @author hao.wang
 */
public class DiffUtils {

    private final static String UPDATE_TEL = "操作人：【{}】, 操作时间：【{}】,  操作表：【{}】, 操作ID:【{}】 执行了【更新】操作。将：【{}】";
    private final static String DEL_TEL = "操作人：【{}】, 操作时间：【{}】, 操作表：【{}】, 执行了【删除】操作。将：【{}】删除了";
    private final static String INFO = "  /{} ：从【{}】改为：【{}】;";


    /**
     * 修改对象比较日志处理
     *
     * @param before 操作前对象
     * @param after  操作后对象
     * @param phone  操作人手机号
     * @param c      操作库名
     * @return
     */
    public static void updateDiff(Object before, Object after, String phone, Class c) {

        Differ differ = DifferFactory.getInstance().getDiffer(c);
        Optional<DiffNode> diff = differ.diff(before, after);
        StringBuffer res = new StringBuffer();
        diff.ifPresent(diffNode -> diffNode.getChildNodes().forEach(a -> {
            String format = StrUtil.format(INFO, a.getProperty(), JSONObject.toJSONString(a.getOriginValue()), JSONObject.toJSONString(a.getTargetValue()));
            res.append(format);

        }));


        if (diff.isPresent()) {
            String id = null;
            try {
                Method m = before.getClass().getMethod("getId");
                m.setAccessible(true);
                Object o1 = m.invoke(before);
                if (o1 != null) {
                    id = o1.toString();
                }
            } catch (IllegalAccessException e) {
            } catch (NoSuchMethodException e) {
            } catch (InvocationTargetException e) {
            }

            String format = StrUtil.format(UPDATE_TEL,
                    phone,
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                    c.getSimpleName(),
                    id,
                    res.toString());

            SpringContext.context().publishEvent(SysLogObject.builder()
                    .phone(phone)
                    .type(0)
                    .dbName(c.getSimpleName())
                    .time(System.currentTimeMillis())
                    .mes(format).build());
        }

    }

    /**
     * 删除对象比较日志处理
     *
     * @param phone 操作人手机号
     * @param c     操作库名
     * @return
     */
    public static void delDiff(Object souce, String phone, Class c) {
        String format = StrUtil.format(DEL_TEL,
                phone,
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                c.getSimpleName(),
                JSONObject.toJSONString(souce));

        SpringContext.context().publishEvent(SysLogObject.builder()
                .phone(phone)
                .type(-1)
                .dbName(c.getSimpleName())
                .time(System.currentTimeMillis())
                .mes(format).build());
    }
}

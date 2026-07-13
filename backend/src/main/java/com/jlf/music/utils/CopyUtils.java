package com.jlf.music.utils;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.SneakyThrows;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 复制工具类
 */
public class CopyUtils {

    // 私有构造函数，防止实例化
    private CopyUtils() {
        throw new IllegalStateException("CopyUtils class");
    }

    /**
     * 将源对象的属性复制到一个新创建的目标对象中。
     * 忽略源对象中在目标对象里没有对应属性
     * @param from 源对象，包含要复制的属性值
     * @param clazz 目标对象的类型
     * @param <T> 目标对象的类型
     * @return 目标对象，包含从源对象复制过来的属性值
     */
    @SneakyThrows // Lombok 会在编译时自动为方法添加必要的 throws 声明 且无需手动捕获或声明异常
    public static <T> T classCopy(Object from, Class<T> clazz) {
        // 使用反射创建目标对象
        T t = clazz.getConstructor().newInstance();
        // 复制源对象的属性到目标对象
        BeanUtils.copyProperties(from, t);
        // 返回目标对象
        return t;
    }

    /**
     * 将源对象的属性复制到目标对象中。
     *
     * @param from 源对象，包含要复制的属性值
     * @param to 目标对象，属性将被复制到此对象
     * @param <T> 目标对象的类型
     */
    @SneakyThrows
    public static <T> void classCopy(Object from, T to) {
        // 复制源对象的属性到目标对象
        BeanUtils.copyProperties(from, to);
    }

    /**
     * 将源对象列表中的每个对象的属性复制到一个新类型的目标对象中，并返回一个新列表。
     *
     * @param from 源对象列表，包含要复制的属性值
     * @param tClass 目标对象的类型
     * @param <T> 源对象的类型
     * @param <E> 目标对象的类型
     * @return 新创建的目标对象列表，包含从源对象列表复制过来的属性值
     */
    @SneakyThrows
    public static <T, E> List<E> classCopyList(List<T> from, Class<E> tClass) {
        // 创建一个新的目标对象列表
        List<E> to = new ArrayList<>();
        // 遍历源对象列表
        for (T t : from) {
            // 使用反射创建目标对象
            E e = tClass.getConstructor().newInstance();
            // 复制源对象的属性到目标对象
            BeanUtils.copyProperties(t, e);
            // 将目标对象添加到新列表中
            to.add(e);
        }
        // 返回目标对象列表
        return to;
    }

    /**
     * 将分页对象中的每个元素的属性复制到新类型的目标对象中，并返回一个新的分页对象。
     *
     * @param in 输入的分页对象，包含要复制的元素列表和分页信息
     * @param eClass 目标元素类型
     * @param <T> 输入的分页对象中的元素类型
     * @param <E> 目标分页对象中的元素类型
     * @return 新的分页对象，包含从源分页对象复制过来的元素以及分页信息
     */
    public static <T, E> IPage<E> covertPage(IPage<T> in, Class<E> eClass) {
        // 创建一个新的分页对象
        IPage<E> out = new Page<>(in.getCurrent(), in.getSize());
        // 设置分页信息
        out.setTotal(in.getTotal());
        out.setPages(in.getPages());
        // 将源分页对象中的记录复制到目标分页对象
        out.setRecords(classCopyList(in.getRecords(), eClass));
        // 返回新的分页对象
        return out;
    }
}

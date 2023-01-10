package com.iserver.starter.esearch.extension;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.regex.Pattern;

/**
 * 搜索通用PO
 *
 * @author Alay
 * @date 2022-03-23 18:02
 */
@Getter
@Setter
public class ISearchParam implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 关键字去除特殊符号
     */
    private static final Pattern PATTERN = Pattern.compile("[\\u00A0\\s\"`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*()——+|{}【】‘；：”“'。，、？]");
    private static final String EMPTY = "";

    /**
     * 关键字
     */
    private String keyword;

    public void setKeyword(String keyword) {
        // 去除关键字的特殊符号
        keyword = PATTERN.matcher(keyword).replaceAll(EMPTY).trim();
        this.keyword = "".equals(keyword) ? null : keyword;
    }

    public String keyword() {
        return this.keyword;
    }

}

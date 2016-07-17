package com.cheng.gu.gc_collapser.data;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Guangcheng.Zhang on 2016/7/13.
 * email:guangcheng.zhang@archermind.com
 */
public class GirlsBean {

    /**
     * error : false
     * results : [{"_id":"57885b8c421aa90de83c1b96","createdAt":"2016-07-15T11:42:04.295Z","desc":"7.15",
     * "publishedAt":"2016-07-15T11:56:07.907Z","source":"chrome","type":"福利","url":"http://ww4.sinaimg
     * .cn/large/610dc034jw1f5ufyzg2ajj20ck0kuq5e.jpg","used":true,"who":"代码家"}]
     */

    private boolean error;
    /**
     * _id : 57885b8c421aa90de83c1b96
     * createdAt : 2016-07-15T11:42:04.295Z
     * desc : 7.15
     * publishedAt : 2016-07-15T11:56:07.907Z
     * source : chrome
     * type : 福利
     * url : http://ww4.sinaimg.cn/large/610dc034jw1f5ufyzg2ajj20ck0kuq5e.jpg
     * used : true
     * who : 代码家
     */

    private List<ResultsBean> results;

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public List<ResultsBean> getResults() {
        return results;
    }

    public void setResults(List<ResultsBean> results) {
        this.results = results;
    }

    public static class ResultsBean implements Serializable {
        private String _id;
        private String createdAt;
        private String desc;
        private String publishedAt;
        private String source;
        private String type;
        private String url;
        private boolean used;
        private String who;

        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public String getPublishedAt() {
            return publishedAt;
        }

        public void setPublishedAt(String publishedAt) {
            this.publishedAt = publishedAt;
        }

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public boolean isUsed() {
            return used;
        }

        public void setUsed(boolean used) {
            this.used = used;
        }

        public String getWho() {
            return who;
        }

        public void setWho(String who) {
            this.who = who;
        }
    }
}

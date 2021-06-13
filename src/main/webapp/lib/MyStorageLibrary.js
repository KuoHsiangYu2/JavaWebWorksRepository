if (window.localStorage === undefined) {
    console.log('瀏覽器不支援 window.localStorage');
}

let MyStorage = function(app) {
    this.app = app;
    this.storage = window.localStorage;
    this.data = JSON.parse(this.storage[this.app] || '{}');
};

MyStorage.prototype = {
    getItem : function(key) {
        return this.data[key];
    },

    setItem : function(key, value) {
        this.data[key] = value;
        this.storage[this.app] = JSON.stringify(this.data);
    },

    deleteItem : function(key) {
        delete this.data[key];
        this.storage[this.app] = JSON.stringify(this.data);
    },

    clear : function() {
        this.data = null;
        this.data = JSON.parse('{}');
        this.storage[this.app] = JSON.stringify(this.data);
    }
};
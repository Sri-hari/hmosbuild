"use strict";function _classCallCheck(e,t){if(!(e instanceof t))throw new TypeError("Cannot call a class as a function")}function _defineProperties(e,t){for(var n=0;n<t.length;n++){var i=t[n];i.enumerable=i.enumerable||!1,i.configurable=!0,"value"in i&&(i.writable=!0),Object.defineProperty(e,i.key,i)}}function _createClass(e,t,n){return t&&_defineProperties(e.prototype,t),n&&_defineProperties(e,n),e}var fs=require("fs"),path=require("path"),output="",initIndexJSONObject={template:{},styles:{},actions:{},data:{}};function compileJson(e,t,n,i,r){e.hooks.done.tap(t+r,function(){"init"===t?writeFileSync(n,initIndexJSONObject):writeFileSync(n,i,r)})}function writeFileSync(e,t,n){if(fs.existsSync(e)){var i=fs.readFileSync(e,{encoding:"utf-8"});try{var r=JSON.parse(i);n&&(r[n]=t||{},fs.writeFileSync(e,stringify(r)))}catch(i){fs.writeFileSync(e,stringify(initIndexJSONObject)),writeFileSync(e,t,n)}}else fs.writeFileSync(e,stringify(t))}function stringify(e){return JSON.stringify(e,null,2)}var AfterEmitPlugin=function(){function e(t){_classCallCheck(this,e),output=t}return _createClass(e,[{key:"apply",value:function(e){e.hooks.afterEmit.tap("delete",function(e){var t=e.assets;Object.keys(t).forEach(function(e){fs.existsSync(path.resolve(output,e))&&fs.unlinkSync(path.resolve(output,e))})})}}]),e}();module.exports={compileJson:compileJson,AfterEmitPlugin:AfterEmitPlugin};
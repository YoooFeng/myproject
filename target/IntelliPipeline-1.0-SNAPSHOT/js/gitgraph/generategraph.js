

// 将git commit记录转化为json
const git2json = require('./git2json.js');

// path需要指定.git所在的路径, 这里作为参数传入
const path = '/home/workplace/Github/IntelliPipeline/target/IntelliPipeline-1.0-SNAPSHOT/WEB-INF/resources/LocalRepo/Shipping-Local/GitResource';

// 指定输出的格式, 使之满足我们的需求
const exportedFields = {
    tag : git2json.defaultFields['refs'],
    branch : git2json.defaultFields[''],

};

// 执行函数, 然后输出结果到控制台
git2json
    .run({ path })
    .then(console.log);
// 将git commit记录转化为json格式的数据
const git2json = require('./git2json.js');
// import git2json from '/js/gitgraph/git2json';

const fs = require('fs');

// TODO: path需要指定.git所在的路径, 这里作为参数传入
const path = '/home/workplace/Github/IntelliPipeline/target/IntelliPipeline-1.0-SNAPSHOT/WEB-INF/resources/LocalRepo/Shipping-Local/GitResource';

const filePath = path.join(path, '..');

getJsonData(path);

function getJsonData(path) {
    git2json
        .run({ path })
        .then(jsonData => writeJsonData(jsonData, 'data'))
        .catch((error) => {
            console.log("Promise error.")
        });
}

/**
 * 将获得的Json数据写入文件中
 * */
function writeJsonData(jsonData, filename) {
    fs.writeFile(`${path}/${filename}.json`, JSON.stringify(jsonData));
}
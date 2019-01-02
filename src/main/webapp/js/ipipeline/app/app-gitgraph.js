// 将git commit记录转化为json
// const git2json = require('../../gitgraph/git2json');
// const GitGraph = require('../../gitgraph/gitgraph.min');
// import git2json from '/js/gitgraph/git2json';
// import GitGraph from '/js/gitgraph/gitgraph';

console.log("This file is invoked");

// TODO: path需要指定.git所在的路径, 这里作为参数传入
const path = '/home/workplace/Github/IntelliPipeline/target/IntelliPipeline-1.0-SNAPSHOT/WEB-INF/resources/LocalRepo/Shipping-Local/GitResource';

// 指定输出的格式, 使之满足我们的需求
// const exportedFields = {
//     tag : git2json.defaultFields['refs'],
//     branch : git2json.defaultFields[''],
// };

var graphConfig = new GitGraph.Template({
    colors: ["#9993FF", "#47E8D4", "#6BDB52", "#F85BB5", "#FFA657", "#F85BB5"],
    branch: {
        color: "#000000",
        lineWidth: 2,
        spacingX: 20,
        mergeStyle: "straight",
        showLabel: true, // display branch names on graph
        labelFont: "normal 10pt Arial",
        labelRotation: 0
    },
    commit: {
        spacingY: -20,
        dot: {
            size: 4,
            strokeColor: "#000000",
            strokeWidth: 2
        },
        tag: {
            font: "normal 10pt Arial",
            color: "yellow"
        },
        message: {
            color: "black",
            font: "normal 12pt Arial",
            displayAuthor: true,
            displayBranch: true,
            displayHash: true,
        }
    },
    arrow: {
        size: 4,
        offset: 1
    }
});

var config = {
    template: graphConfig,
    mode: "extended",
    orientation: "vertical-reverse",
    // 是否显示文字信息
    mode: "compact",
    elementId : "gitGraph"
};

function generateGitGraph() {
    console.log("gitGraph");

    var gitGraph = new GitGraph(config);

    var master = gitGraph.branch("master");
    gitGraph.commit("init commit");
    master.commit("new branch");

}

function dataProcess(data) {
    var gitGraph = new GitGraph(config);

    // 时间排序
    data = data.sort(function(a, b) {
        return a.author.timestamp - b.author.timestamp;
    });

    // 添加child\ tag\ branch信息
    var dataMap = {};

    data.forEach(function(a) {
        dataMap[a.hash] = a;
    });

    data.forEach(function(a) {
        for (var i = 0; i < a.parents.length; i++) {
            if (!dataMap[a.parents[i]].child) {
                dataMap[a.parents[i]].child = [];
            }
            dataMap[a.parents[i]].child.push(a.hash);
        }

        // 获取分支信息
        if (a.refs.length > 0) {
            for (var i = 0; i < a.refs.length; i++) {
                if (isTag(a.refs[i])) {
                    // 是tag, 直接加上
                    dataMap[a.hash]['tag'] = a.refs[i];
                }

                if (isBranch(a.refs[i])) {
                    dataMap[a.hash]['branch'] = a.refs[i];
                    var branch = a.refs[i];

                    if (a.parents.length == 0) continue;

                    var parent = dataMap[a.parents[0]];
                    while (true) {

                        dataMap[parent.hash]['branch'] = branch;

                        if (parent.parents.length == 0) break;

                        parent = dataMap[parent.parents[0]];
                    }
                }
            }
        }
    });

    // 接下来给没名字的分支命名
    data = data.sort(function(a, b) {
        return b.author.timestamp - a.author.timestamp;
    });

    data.forEach(function(a) {
        if (a.branch) return;

        dataMap[a.hash]['branch'] = a.hashAbbrev;

        var branch = a.hashAbbrev;

        if (a.parents.length == 0) return;

        var parent = dataMap[a.parents[0]];
        while (true) {
            if (!dataMap[parent.hash].branch) {
                dataMap[parent.hash].branch = branch;
            }

            if (parent.parents.length == 0) break;

            parent = dataMap[parent.parents[0]];
        }
    });

    var branchMap = {};

// 按时间顺序一个个commit
    data = data.sort(function(a, b) {
        return a.author.timestamp - b.author.timestamp;
    });

// 渲染很慢, 这里只用200个commit
    for (var i = 0; i < 300; i++) {
        var commit = data[i];

        var branchName = commit.branch;

        if (!branchMap[branchName]) {
            if (commit.parents.length > 0) {
                branchMap[branchName] = gitGraph.branch({
                    name: branchName,
                    parentBranch: branchMap[dataMap[commit.parents[0]].branch],
                });
            } else {
                branchMap[branchName] = gitGraph.branch({
                    name: branchName
                });
            }
        }

        var commitConfig = {
            sha1: commit.hashAbbrev,
            message: commit.subject,
            author: commit.author.name + commit.author.email,
        };
        if (commit.tag) {
            commitConfig['tag'] = commit.tag;
            console.log('tag ' + commitConfig);
        }
        // merge
        if (commit.parents.length > 1) {
            for (var j = 1; j < commit.parents.length; j++) {
                var branch1 = branchMap[branchName];
                var branch2 = branchMap[dataMap[commit.parents[j]].branch];

                branch2.merge(branch1, commitConfig);
            }
            console.log('merge');
            console.log(branch1);
            console.log(branch2);
            // merge之后删除临时分支
            branch2.delete();
        } else {
            branchMap[branchName].commit(commitConfig);
        }
    }
}

function isTag(tag) {
    return tag.indexOf('tag');
}

function isBranch(branch) {
    if (branch.match(/HEAD/g) || branch.match(/origin\/HEAD/g)) return false;
    if (branch.match(/origin.*/g)) return true;
}

dataProcess(data);
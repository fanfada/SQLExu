<template>
    <div>
        <p>请输入要执行的命令:</p>
        <input type="text" placeholder="请输入命令" id="commandId" v-model.lazy="command"><br>
        <!-- <label for="text">{{ command }}</label><br> -->
        <button @click="execuHandle" class="execu">执行</button>
        <button @click="stopHandle" class="stop">停止</button>
        <div>
            <p id="result">{{ result }}</p>
            <pre v-if="rawData">{{ rawData }}</pre>
            <p v-else>加载中...</p>
        </div>
    </div>
</template>

<script>
import axios from "axios";

export default {
    data() {
        return {
            command: "",
            rawData: "",
            result: ""
        }
    },
    methods: {
        execuHandle() {
            this.result = this.command + " 命令执行结果："
            console.log(this.command);
            const data = {
                command: this.command,
            };
            const headers = {
                'Content-Type': 'application/json',
            };
            axios.post('/api/executeCommand', data, { headers: headers })
                .then(response => {
                    console.log(this.command)
                    // console.log('Success:', response);
                    console.log(response.data)
                    this.rawData = response.data
                    this.rawData = this.rawData.replace("Command executed:", "Command executed:\n")
                    // 处理响应数据
                })
                .catch(function (error) {
                    // 请求失败处理
                    console.log(error);
                });
        },
        stopHandle(){

        }
        
    }
}

</script>

<style>
#result {
    margin-right: 5px;
    font-size: 20px;
}

.execu {
    margin-top: 15px;
    margin-right: 5px;
}
.stop {
    margin-top: 15px;
    margin-left: 5px;
}

pre {
    font-size: 15px;
    font-family: 'Courier New', monospace;
    /* 使用等宽字体 */
    background-color: #f4f4f4;
    /* 背景色 */
    padding: 16px;
    /* 内边距 */
    border: 1px solid #ddd;
    /* 边框 */
    white-space: pre-wrap;
    /* 保持换行并且内容自动换行 */
    word-wrap: break-word;
    /* 超过容器宽度的内容会换行 */
}
</style>
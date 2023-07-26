export const statusFormatter = (val) => {
    return val === false ? "否" : "是";
}

export const yesOrNo = [{label: "是", value: true}, {label: "否", value: false}]


export const replyStatusFormatter = (val) => {
    return val === false ? "失败" : "成功";
}

export const replaySuccessOrFail = [{label: "成功", value: true}, {label: "失败", value: false}]
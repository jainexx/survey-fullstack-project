<template>
    <div v-if="visible" class="alert-dialog-overlay">
        <div class="alert-dialog">
            <slot></slot>
            <button @click="confirm" class="confirm-btn">返回</button>
        </div>
    </div>
</template>

<script>
export default {
    props: {
        visible: {
            type: Boolean,
            default: false,
        },
    },
    methods: {
        confirm() {
            this.$emit("confirm");
        },
    },
};
</script>

<style scoped lang="scss">
* {
    font-family: "Roboto", sans-serif;
    font-weight: 400;
    font-style: normal;
    letter-spacing: 0.2rem;
}
.alert-dialog-overlay {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: rgba(0, 0, 0, 0.9);
    display: flex;
    justify-content: center;
    align-items: center;
}
.alert-dialog {
    background-color: $orange;
    padding: 20px;
    border-radius: 5px;
    text-align: center;
    width: 300px;
    height: 300px;
    display: flex;
    flex-direction: column;
    justify-content: center;
}
.confirm-btn {
    margin: 0 auto;
    margin-top: 20px;
    padding: 10px 20px;
    width: 50%;
    background-color: $black;
    color: $bgColor;
    border: none;
    border-radius: 5px;
    cursor: pointer;
}
</style>

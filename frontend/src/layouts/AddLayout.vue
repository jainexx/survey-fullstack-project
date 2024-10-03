<script>
import { RouterLink, RouterView } from "vue-router";
import MinorHeader from "@/components/MinorHeader.vue";

export default {
    components: {
        RouterLink,
        RouterView,
        MinorHeader,
    },
};
</script>

<template>
    <div class="layout">
        <div class="header">
            <MinorHeader />
        </div>

        <div class="content">
            <RouterView />
        </div>
    </div>
</template>

<style scoped lang="scss">
* {
    font-family: "Roboto", sans-serif;
    font-weight: 400;
    font-style: normal;
    letter-spacing: 0.2rem;
}


.layout {
    width: 100dvw;
    height: 100dvh;
    display: grid;
    grid-template-columns: repeat(12, 1fr);
    grid-template-rows: repeat(9, 1fr);

    .header {
        grid-area: 1 / 1 / 2 / 13;
    }

    .content {
        grid-area: 2 / 2 / 11 / 13;
        width: 90%;
        margin: 0 auto;
        margin-top: 1rem;
        // border: 2px solid black;
    }
}
</style>

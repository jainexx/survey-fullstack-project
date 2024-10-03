<script>
import { RouterLink } from "vue-router";
</script>

<template>
    <div class="viewContainer">
        <div class="progress">
            <p>50 / <span class="hundred">100</span></p>
        </div>
        <h1 class="title">
            <span>MAKE YOUR</span><br />
            <span class="voice">VOICE</span><br />
            <span>HEARD</span>
        </h1>
        <div class="startSurvey">
            <p>start the <span class="survey">survey.</span></p>
        </div>
        <div class="buttonsContainer">
            <RouterLink to="/authuser" class="linkClass">問卷管理 </RouterLink>
            <div class="divider"></div>
            <RouterLink to="/unauth" class="linkClass"> 填寫問卷</RouterLink>
        </div>
    </div>
</template>

<style scoped lang="scss">
* {
    font-family: "Roboto", sans-serif;
    font-weight: 700;
    font-style: italic;
    color: $black;
    letter-spacing: 0.2rem;
}
.viewContainer {
    background-color: $bgColor;
    width: 100dvw;
    height: 100dvh;
    display: grid;
    grid-template-columns: repeat(12, 1fr);
    grid-template-rows: repeat(8, 1fr);

    .progress {
        // border: 2px solid black;
        grid-area: 2 / 2 / 3 / 4;
        font-size: 3rem;
        align-self: self-end;
        .hundred {
            color: $orange;
        }
    }
    .title {
        // border: 2px solid black;
        grid-area: 3 / 2 / 7 / 8;
        font-size: 7.5rem;
        .voice {
            font-family: "Roboto", sans-serif;
            font-weight: 100;
            position: relative;
            &::after {
                content: "VOICE";
                position: absolute;
                left: 0.5rem;
            }
        }
    }
    .startSurvey {
        grid-area: 4 / 6 / 5 / 12;
        border-top: 2px solid black;
        font-size: 2.5rem;
        align-self: self-end;
        text-align: center;
        .survey {
            position: relative;
            z-index: 1;
            &::before {
                content: "";
                width: 10rem;
                height: 1.5rem;
                z-index: -1;
                background-color: $orange;
                position: absolute;
                top: 0.5rem;
            }
        }
    }
    .buttonsContainer {
        grid-area: 5 / 7 / 7 / 11;
        // border: 2px solid black;
        font-size: 2.5rem;
        display: flex;
        flex-wrap: wrap;
        justify-content: center;
        align-items: end;
        margin: 0.5rem;
        background-color: $green;
        border-radius: 5px;

        .linkClass {
            text-decoration: none;
            letter-spacing: 0.2dvw;
            font-style: normal;
            margin-bottom: 2rem;
        }
        .divider {
            width: 0.2dvw;
            height: 100%;
            background-color: $black;
            margin: 0 1rem;
        }
    }
}
</style>

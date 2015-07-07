<style lang="scss">
  @import "../../monitor.scss";

  #menus {
    width: 100%;
    @extend %layout; 
    flex-direction: column; 
    padding: 0px;
    svg { color: $front-color; }
    li { @extend %layout; @extend %center; list-style-type: none; margin-bottom: 3px; }
    svg { width: 3rem; height: 3rem; }
  }
</style>

<template lang="jade">
  mixin item(id)
    li: a: svg: use(xlink:href="#{id}")

  ul#menus
    +item("#meter")
    +item("#user")
    +item("#cog")
    +item("#question")
    +item("#switch")

</template>

<script type="text/javascript">
  module.exports = {
    data: {

    }
  }
</script>
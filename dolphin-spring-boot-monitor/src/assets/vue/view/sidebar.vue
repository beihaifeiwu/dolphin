<style lang="scss">
  @import "../../monitor.scss";

  #menus {
    width: 100%;
    @extend %layout; 
    flex-direction: column; 
    padding: 0px;
    svg { color: $front-color; }
    li { @extend %layout; @extend %center; list-style-type: none; margin-bottom: 1rem; }
    svg { width: 2.5rem; height: 2.5rem; }
  }
</style>

<template lang="jade">
  mixin item(id)
    li: a: svg.icon(class="#{id}"): use(xlink:href="##{id}")

  ul#menus
    +item("icon-meter")
    +item("icon-user")
    +item("icon-cog")
    +item("icon-question")
    +item("icon-switch")

</template>

<script type="text/javascript">
  module.exports = {
    data: {

    }
  }
</script>
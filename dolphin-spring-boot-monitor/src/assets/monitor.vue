<style lang="scss">
  @import "monitor.scss";

  %left { width: 5rem; margin-right: 2px; }
  %right { flex: 1 1 auto; }
  %top { flex-direction: row; height: 4rem; width: 100%; background-color: $back-color; }
  %down { flex-direction: row; height: 100%; width: 100%; }

  %area { background-color: $float-color; }

  #container {
    @extend %layout;
    flex-direction: column;
    align-items: center;
    width: 100%; height: 100%;

    #header-container {
      @extend %layout;
      @extend %top;

      #icon {
        @extend %layout;
        @extend %left;
        @extend %area;
        justify-content: center;
        img {
          width: 4rem; height: 3.5rem;
        }
      }

      #header {
        @extend %layout;
        @extend %right;
        @extend %area;
      }
    }

    #body-container {
      @extend %layout;
      @extend %down;

      #sidebar {
        @extend %layout;
        @extend %left;
        @extend %area;
        flex-direction: column;
        margin-top: 2px; 

      }

      #content {
        @extend %layout;
        @extend %right;
        background-color: $content-back-color;
        margin-top: 2px; 
      }

    }

  }

</style>

<template lang="jade" >

  #container
    #header-container
      #icon
        img(src="{{logo}}")
      #header
        div(v-component="headerView")
    #body-container
      #sidebar
        div(v-component="sidebarView")
      #content Main content  
                  
</template>

<script type="text/javascript">
	module.exports = {
		el: '#monitor',
		replace: true,
		data: {
      logo: require('./clean-fire.svg')
		},
		components: {
			headerView: require('./vue/view/header.vue'),
      sidebarView: require('./vue/view/sidebar.vue')
		}
	}
</script>
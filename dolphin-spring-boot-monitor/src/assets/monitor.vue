<style lang="postcss">
  @custom-selector :--basic html, body;
  @custom-selector :--layout-area section#icon, section#header, aside#sidebar, section#content; 

  @import "monitor.css";

  :--basic { margin: 0; padding: 0; height: 100%; width: 100%; background-color: var(--back-color); color: #ddd; }
  
  :--layout-area { border: 1px solid red; }

  #container { display: flex; flex-direction: column; height: 100%; width: 100%; }
  #header-container { display: flex; flex-flow: row nowrap; height: 4rem; }
  #body-container { display: flex; flex-flow: row nowrap; height: 100%; }
  #icon, #sidebar { display: flex; width: 5rem; justify-content: center; align-items: center; }
  #header, #content { flex: auto; }

  #icon > img { width: 4rem; height: 3.5rem; }
</style>

<template>
  
  <div id="container">
	  <header id="header-container">
		  <section id="icon">
		  	<img src="{{iconSrc}}" />
		  </section>
		  <section id="header">
		  	<div v-component="headerView"></div>	
		  </section>
	  </header>
	  <section id="body-container">
		  <aside id="sidebar">Sidebar</aside>
		  <section id="content">Main content</section>
	  </section>
  </div>

</template>

<script type="text/javascript">
	module.exports = {
		el: '#monitor',
		replace: true,
		data: {
			iconSrc: require("./imgs/clean-fire.svg")
		},
		components: {
			headerView: require('./vue/view/header.vue')
		}
	}
</script>
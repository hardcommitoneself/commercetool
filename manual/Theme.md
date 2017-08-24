# Theme

## Structure
All theme files are loaded as a [WebJars](http://www.webjars.org/) dependency to Sunrise. This includes [Handlebars](http://handlebarsjs.com/) source templates, all web assets (i.e. CSS, JS, images and fonts) and i18n [YAML](http://www.yaml.org/) files. The expected structure being as follows:

```
META-INF
+-- resources
    +-- webjars
        +-- css
        +-- fonts
        +-- img
        +-- js
        +-- i18n
        |   +-- de
        |   +-- en
        |   +-- ..
        +-- templates
```

### Template
Sunrise uses [Handlebars.java](https://jknack.github.io/handlebars.java/) by default as a template engine.

In order to find the corresponding template source file, it searches first inside the classpath `/templates`. If the file is not found there, then it tries inside the Theme's Webjars dependency (i.e. `/META-INF/resources/webjars/templates`). This enables a practical way to override parts of the template without the need of replacing it completely, as we will see in the section _[Customize HTML](#customize-html)_.

Learn how to modify this behaviour in _[Change template source loaders](Configuration.md#change-template-source-loaders)_.

### Web Assets
By calling the `AssetsController` in your routes, your application will first try to load the requested web asset inside Play's `public` folder and then inside the Theme's Webjars. If no web asset with the given path is found, it will return a 404 status error.

Notice that by placing web assets in Play's `public` folder you can easily extend Sunrise's functionality, as explained in _[Customize Web Assets](#customize-web-assets)_.

### Internationalization
Sunrise uses [YAML](http://www.yaml.org/) files by default to provide text in different languages. Translations are grouped according to the page or section they belong, which is known as bundles (e.g. `home`, `checkout`). Each YAML file contains the translated text for a particular language and bundle.

The following structure would be used to have translations in German and English for the home and checkout bundles:

```
i18n
+-- de
|   +-- home.yaml
|   +-- checkout.yaml
+-- en
    +-- home.yaml
    +-- checkout.yaml
```

Similarly as it works with templates, the application tries to find the translated text first inside the classpath `/i18n`. If that particular translation is not found there, then it tries inside the Template's Webjars dependency (i.e. `/META-INF/resources/webjars/i18n`). This enables a practical way to override a particular translation without the need of replacing them all, as it is explained in the section _[Customize Internationalization](#customize-internationalization)_.

Learn how to modify this behaviour in _[Change i18n resource loaders](Configuration.md#change-i18n-source-loaders)_.


## Customization

This guide shows you how to customize different parts of the template in an easy and convenient way. Note it is assuming the configuration has not been changed and that you have installed the [Theme Importer SBT Plugin](https://github.com/commercetools/commercetools-sunrise-java-theme-importer).

### Customize HTML

#### Modify a template

Run Sunrise and open it in a browser to inspect the part of the code you want to change. In the website's source code you will find comments indicating the template source file containing each component. In the following example we can see that the template source file containing the logo is `common/logo.hbs`.

```html
...
<div class="col-sm-8">
  <!-- start common/logo.hbs -->
  <a href="/en/home" class="brand-logo">
    <img class="img-responsive" src="/assets/img/logo.svg" alt="SUNRISE">
  </a>
  <!-- end common/logo.hbs -->
</div>
...
```

In order to override this component we need to create a Handlebars template source file in `conf/templates` with the desired content, so that the new code is used instead of the original code. To do so, [Sunrise Theme Importer](https://github.com/commercetools/commercetools-sunrise-java-theme-importer) offers a convenient [SBT](http://www.scala-sbt.org/) command `sunriseThemeImportTemplateFiles`, which given a whitespace-separated list of template files copies them from the original location to `conf/templates`.

Let's execute it for the previous example:

```shell
sbt 'sunriseThemeImportTemplateFiles common/logo.hbs'
```

After the execution, `conf/templates/common/logo.hbs` has been created with the content of the original template source file:

```hbs
<!-- start common/logo.hbs -->
<a href="{{@root.meta._links.home.href}}" class="brand-logo">
  <img class="img-responsive" src="{{@root.meta.assetsPath}}img/logo.svg" alt="SUNRISE">
</a>
<!-- end common/logo.hbs -->
```

With that, you are ready to start modifying the template. For example, let's replace the logo image with a simple text:

```hbs
<!-- start common/logo.hbs -->
<a href="{{@root.meta._links.home.href}}" class="brand-logo">
  Sunrise
</a>
<!-- end common/logo.hbs -->
```

If you run Sunrise and reload the page, the image has been effectively replaced by the text:

```html
...
<div class="col-sm-8">
  <!-- start common/logo.hbs -->
  <a href="/en/home" class="brand-logo">
    Sunrise
  </a>
  <!-- end common/logo.hbs -->
</div>
...
```

To learn how to write Handlebars templates, please check the [Handlebars.js](http://handlebarsjs.com/) documentation. In particular, the sections about [Expressions](http://handlebarsjs.com/expressions.html), [Built-In Helpers](http://handlebarsjs.com/builtin_helpers.html) and [@data Variables](http://handlebarsjs.com/reference.html#data).

#### Modify HTML `<head>`
You may need to provide additional HTML `<meta>` tags or other kind of information to the HTML `<head>`. To do so, just add them to the template source file `conf/templates/common/additional-html-head.hbs`, as shown in the example:

```hbs
<link rel="stylesheet" href="/assets/public/stylesheets/sunrise.css"/> <!-- default sunrise CSS file -->
<meta name="description" content="My description"/> <!-- your meta tag -->
```

### Customize Web Assets

#### Change images
If you want to provide your own images, you just have to place them inside the `public/images/` folder and modify the HTML accordingly as explained in the section _[Customize HTML](#customize-html)_. Following the logo example used in that section, you should obtain:

```hbs
<!-- start common/logo.hbs -->
<a href="{{@root.meta._links.home.href}}" class="brand-logo">
    <img class="img-responsive" src="{{@root.meta.assetsPath}}public/images/yourlogo.png" alt="YOUR SITE">
</a>
<!-- end common/logo.hbs -->
```

Notice that the image path has been replaced to `public/images/`.

#### Modify CSS
Sunrise comes with the file `public/stylesheets/sunrise.css` where you can add your own CSS rules. As this is the last CSS file loaded of the website, from here you can override any previous rule set by the template.

If you want to provide your own CSS file instead, you just have to place the file inside the `public/stylesheets/` folder and add the HTML `<link>` tag in the template source file `conf/templates/common/additional-html-head.hbs`, as shown in the example:

```hbs
<link rel="stylesheet" href="/assets/public/stylesheets/sunrise.css"/> <!-- default sunrise CSS file -->
<link rel="stylesheet" href="/assets/public/stylesheets/yourfile.css"/> <!-- your CSS file -->
```

#### Modify JavaScript
Sunrise comes with the file `public/javascripts/sunrise.js` where you can add your own JavaScript code.

If you want to provide your own JavaScript file instead, you just have to place the file inside the `public/javascripts/` folder and add the HTML `<script>` tag in the template source file `conf/templates/common/additional-html-scripts.hbs`, as shown in the example:

```hbs
<script src="/assets/public/javascripts/sunrise.js"></script> <!-- default sunrise JS file -->
<script src="/assets/public/javascripts/yourfile.js"></script> <!-- your JS file -->
```

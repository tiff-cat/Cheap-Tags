# Cheap-Tags
This was a group project with three other people.  
We made a photo managing application where users can import photos, and organize them by adding tags. Users can import photos from local directories, Instagram, or Tumblr. Images can be organized by adding and removing tags, and searching by name on a regex search bar. Images can be reuploaded to Instagram with a caption that will feature the added tags.

This project features Java, JavaFX, REST API, regex, and git. 

### Home
On the home screen, users can view images by choosing a local directory, importing from Instagram, Tumblr, or clicking the recently viewed shortcuts on the left hand side. They can also edit their collections of tags by clicking My Tags, or view any changes ever made to images by clicking the Master Log.  

![home screen](/images/home.png)

### Editing tags
On the tag screen, users can edit their collection of tags, and search (by regex) from the list of tags.  

![tag screen](/images/tag.png)

### Editing images
Images can be viewed by selecting from the list on the left. Once tags are added to the image, its name will be changed to include the tag both in the program and in the local directory it is stored. 

![add tag to image](/images/addtag.png)

### Uploading to Instagram
An image can be uploaded to Instagram with a caption. Existing tags on that image will be featured as well.

![upload to insta](/images/upload.png)

![verify insta](/images/instagramdemo.png)

### Revert to an old name
The revision log allows the user to view the edit history of an image, and revert back to any previous name.

![revert](/images/revert.png)

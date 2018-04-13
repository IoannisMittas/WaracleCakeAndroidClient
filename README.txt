Major changes:

- Implemented the cake browser with a RecyclerView.

- Added Cake class to hold the cake data.

- Changed network code.

- Used AsynchTask to avoid network calls on main thread.

- Changed RelativeLayout to LinearLayout in list_item_layout as it seemed a better choice for our UI.

- If third party libraries were allowed, I would use Picasso or Glide for image processing,
  okhttp3 for network calls,and maybe guavafor this line of code: byte[] bytes = ByteStreams.toByteArray(inputStream); :)


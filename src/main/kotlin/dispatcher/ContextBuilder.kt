package dispatcher

import dto.ContextHolder

interface ContextBuilder {

    val builder: (JsonHolder?) -> ContextHolder?

}
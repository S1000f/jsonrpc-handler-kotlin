package dispatcher

import dto.ContextHolder

interface ContextBuilder {
    fun build(jsonHolder: JsonHolder?): ContextHolder?
}